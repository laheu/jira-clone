# Jira Clone (On-Premise, Read-Only)

Ein leichtgewichtiger Jira-Nachbau mit Basis-Funktionalität, der **direkt lesend** auf der
Oracle-Datenbank eines bestehenden **Jira 10.x (Data Center)** arbeitet. Das originale Jira
kann parallel weiterlaufen – diese Anwendung führt **keinerlei Schreiboperationen** auf der
Datenbank aus.

## Funktionsumfang (MVP)

- **Login** mit einem einzelnen, lokal konfigurierten Benutzer (kein LDAP). Dieser Benutzer
  sieht alle Projekte und Vorgänge – Jira-Berechtigungsschemata werden nicht ausgewertet.
- **Projektübersicht** mit Projektleitung und Vorgangsanzahl
- **Vorgangsliste** pro Projekt mit Filtern (Status, Typ – nur die im Projekt tatsächlich
  vorkommenden Werte), Volltextsuche auf der Zusammenfassung und Paginierung
- **Vorgangsdetails**: Beschreibung, Bearbeiter/Autor (aufgelöst über `app_user`/`cwd_user`),
  Priorität, Lösung, Labels, Kommentare
- **Hierarchie** (z. B. Initiative > Epic > Story): übergeordneter Vorgang und Liste der
  untergeordneten Vorgänge. Ausgewertet werden Epic-Story-/Subtask-Issue-Links sowie die
  Custom Fields „Epic Link" und „Parent Link" (Advanced Roadmaps).
- **Anhänge**: Auflistung und Download/Anzeige (Metadaten aus `fileattachment`, Dateien aus
  Jiras Attachment-Verzeichnis); Bilder werden als Vorschau und – bei Wiki-Referenzen wie
  `!screenshot.png!` – inline in Beschreibung/Kommentaren angezeigt
- **Workflow-Übergänge (nur Anzeige)**: Die möglichen Transitionen des aktuellen Status
  werden aus dem OSWorkflow-XML-Descriptor (`jiraworkflows`) geparst und angezeigt –
  ausführen lassen sie sich bewusst nicht.

## Architektur

```
backend/   Spring Boot 3 (Java 21), Spring JDBC, Spring Security – REST-API unter /api
frontend/  React + Vite + TypeScript – Single-Page-App
```

- Der Zugriff auf die Jira-Tabellen (`project`, `jiraissue`, `jiraaction`, `issuestatus`,
  `issuetype`, `priority`, `resolution`, `label`, `app_user`, `cwd_user`, `os_currentstep`,
  `jiraworkflows`, `workflowscheme`, `workflowschemeentity`, `nodeassociation`) erfolgt über
  explizite SQL-Queries mit `JdbcTemplate` – ausschließlich `SELECT`.
- Der Issue-Key wird wie in Jira 10 aus `project.pkey` + `jiraissue.issuenum` gebildet.
- Zwei Spring-Profile:
  - **`dev`** (Default): In-Memory-H2 im Oracle-Kompatibilitätsmodus mit einem nachgebauten
    Subset des Jira-Schemas und Demodaten → komplett standalone lauffähig.
  - **`prod`**: Verbindung zur echten Jira-Oracle-DB.

## Standalone testen (dev-Profil)

Voraussetzungen: Java 21 und Node.js 20+ (Maven wird nicht benötigt – der mitgelieferte
Maven Wrapper `./mvnw` lädt die passende Version automatisch herunter).

```bash
# Terminal 1 – Backend (Port 8080)
cd backend
./mvnw spring-boot:run

# Terminal 2 – Frontend mit Hot-Reload (Port 5173, Proxy auf 8080)
cd frontend
npm install
npm run dev
```

Dann <http://localhost:5173> öffnen und mit **`admin` / `admin`** anmelden
(konfiguriert in `backend/src/main/resources/application-dev.yml`).

Alternativ als ein einzelnes Artefakt:

```bash
cd frontend && npm install && npm run build   # baut nach backend/src/main/resources/static
cd ../backend && ./mvnw package
java -jar target/jira-clone-backend-0.1.0-SNAPSHOT.jar   # alles unter http://localhost:8080
```

## Betrieb gegen die echte Jira-Oracle-DB (prod-Profil)

> **Wichtig:** Da das originale Jira parallel läuft, darf diese Anwendung nur lesen.
> Legt dafür einen dedizierten Oracle-Benutzer an, der ausschließlich `SELECT`-Grants
> (bzw. Synonyme) auf das Jira-Schema besitzt. Zusätzlich setzt der Connection-Pool
> `read-only`-Verbindungen ein.

```bash
export SPRING_PROFILES_ACTIVE=prod
export JIRA_DB_URL="jdbc:oracle:thin:@//dbhost:1521/JIRAPDB"
export JIRA_DB_USERNAME="jira_readonly"
export JIRA_DB_PASSWORD="..."
export APP_AUTH_USERNAME="admin"
export APP_AUTH_PASSWORD='{bcrypt}$2a$10$...'   # Hash z. B. erzeugen mit: htpasswd -bnBC 10 "" passwort

java -jar jira-clone-backend-0.1.0-SNAPSHOT.jar
```

| Variable            | Bedeutung                                                        |
| ------------------- | ---------------------------------------------------------------- |
| `JIRA_DB_URL`       | JDBC-URL der Jira-Oracle-Datenbank                               |
| `JIRA_DB_USERNAME`  | Read-only-DB-Account (nicht der Jira-Anwendungsbenutzer!)        |
| `JIRA_DB_PASSWORD`  | Passwort des DB-Accounts                                         |
| `APP_AUTH_USERNAME` | Login-Name des einen Anwendungsbenutzers                         |
| `APP_AUTH_PASSWORD` | Passwort mit Encoder-Präfix, z. B. `{bcrypt}…` (`{noop}…` nur für Tests) |
| `JIRA_ATTACHMENTS_DIR` | Optional: Pfad zu Jiras Attachment-Verzeichnis (`<jira-home>/data/attachments`), z. B. ein read-only NFS-Mount des Jira-Servers. Leer = Anhang-Download deaktiviert. |

> **Hinweis zu Anhängen:** Jira speichert Anhänge nicht in der Datenbank, sondern im
> Dateisystem unter `<jira-home>/data/attachments`. Damit der Clone sie ausliefern kann,
> muss dieses Verzeichnis lesbar gemountet und über `JIRA_ATTACHMENTS_DIR` bekannt sein.
> Unterstützt werden beide Jira-Verzeichnislayouts (`<KEY>/<bucket>/<KEY-Nr>/<id>` ab
> Jira 8.1 sowie das ältere `<KEY>/<KEY-Nr>/<id>`).

## Tests

```bash
cd backend && ./mvnw test
```

Enthalten: Repository-Integrationstests gegen das H2-Dev-Schema (Key-Bildung, Filter,
Paging, Kommentar-/Label-Queries), Unit-Tests für den OSWorkflow-Descriptor-Parser sowie
API-/Security-Tests (Login-Flow, 401/404).

## Bekannte Grenzen / spätere Ausbaustufen

- Keine Schreiboperationen (geplant: über die REST API des laufenden Jira)
- Keine Auswertung von Berechtigungsschemata, keine Custom Fields, Anhänge, Boards oder JQL
- Das Jira-Standard-Workflow-Schema („jira“, nicht in der DB gespeichert) liefert keine
  Transitionen – es wird dann nur der aktuelle Status angezeigt
- Kein LDAP/AD-Login (bewusst: ein lokaler Testbenutzer)
