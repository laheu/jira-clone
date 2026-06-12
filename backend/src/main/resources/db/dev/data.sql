-- Demo data for standalone development. Mirrors typical Jira 10.x content:
-- classic statuses/types/priorities, three projects, ~20 issues, comments,
-- labels and a classic OSWorkflow descriptor.

-- ---------------------------------------------------------------- lookups
INSERT INTO issuestatus (id, sequence, pname, description, iconurl, statuscategory) VALUES ('1', 1, 'Open', 'Der Vorgang ist offen.', NULL, 2);
INSERT INTO issuestatus (id, sequence, pname, description, iconurl, statuscategory) VALUES ('3', 3, 'In Progress', 'Der Vorgang wird gerade bearbeitet.', NULL, 4);
INSERT INTO issuestatus (id, sequence, pname, description, iconurl, statuscategory) VALUES ('4', 4, 'Reopened', 'Der Vorgang wurde wieder geoeffnet.', NULL, 2);
INSERT INTO issuestatus (id, sequence, pname, description, iconurl, statuscategory) VALUES ('5', 5, 'Resolved', 'Eine Loesung wurde eingetragen.', NULL, 3);
INSERT INTO issuestatus (id, sequence, pname, description, iconurl, statuscategory) VALUES ('6', 6, 'Closed', 'Der Vorgang ist abgeschlossen.', NULL, 3);

INSERT INTO issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) VALUES ('1', 1, 'Bug', NULL, 'Ein Fehler, der das Produkt beeintraechtigt.', NULL, NULL);
INSERT INTO issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) VALUES ('3', 2, 'Task', NULL, 'Eine Aufgabe, die erledigt werden muss.', NULL, NULL);
INSERT INTO issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) VALUES ('10001', 3, 'Story', NULL, 'Eine Anforderung aus Nutzersicht.', NULL, NULL);
INSERT INTO issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) VALUES ('10000', 4, 'Epic', NULL, 'Ein grosses Arbeitspaket.', NULL, NULL);
INSERT INTO issuetype (id, sequence, pname, pstyle, description, iconurl, avatar) VALUES ('10200', 5, 'Initiative', NULL, 'Strategisches Thema oberhalb von Epics.', NULL, NULL);

INSERT INTO priority (id, sequence, pname, description, iconurl, status_color) VALUES ('1', 1, 'Blocker', 'Blockiert den Fortschritt.', NULL, '#d04437');
INSERT INTO priority (id, sequence, pname, description, iconurl, status_color) VALUES ('2', 2, 'Critical', 'Absturz oder Datenverlust.', NULL, '#e3493b');
INSERT INTO priority (id, sequence, pname, description, iconurl, status_color) VALUES ('3', 3, 'Major', 'Wesentlicher Funktionsverlust.', NULL, '#f79232');
INSERT INTO priority (id, sequence, pname, description, iconurl, status_color) VALUES ('4', 4, 'Minor', 'Geringer Funktionsverlust.', NULL, '#2a8735');
INSERT INTO priority (id, sequence, pname, description, iconurl, status_color) VALUES ('5', 5, 'Trivial', 'Kosmetisches Problem.', NULL, '#707070');

INSERT INTO resolution (id, sequence, pname, description, iconurl) VALUES ('1', 1, 'Fixed', 'Behoben.', NULL);
INSERT INTO resolution (id, sequence, pname, description, iconurl) VALUES ('2', 2, 'Won''t Fix', 'Wird nicht behoben.', NULL);
INSERT INTO resolution (id, sequence, pname, description, iconurl) VALUES ('3', 3, 'Duplicate', 'Duplikat eines anderen Vorgangs.', NULL);

-- ---------------------------------------------------------------- users
INSERT INTO app_user (id, user_key, lower_user_name) VALUES (10100, 'lars', 'lars');
INSERT INTO app_user (id, user_key, lower_user_name) VALUES (10101, 'anna.schmidt', 'anna.schmidt');
INSERT INTO app_user (id, user_key, lower_user_name) VALUES (10102, 'tom.weber', 'tom.weber');
INSERT INTO app_user (id, user_key, lower_user_name) VALUES (10103, 'JIRAUSER10103', 'maria.lopez');

INSERT INTO cwd_user (id, directory_id, user_name, lower_user_name, active, created_date, updated_date, first_name, lower_first_name, last_name, lower_last_name, display_name, lower_display_name, email_address, lower_email_address, external_id, credential)
VALUES (10200, 1, 'lars', 'lars', 1, TIMESTAMP '2020-01-15 09:00:00', TIMESTAMP '2020-01-15 09:00:00', 'Lars', 'lars', 'Heucke', 'heucke', 'Lars Heucke', 'lars heucke', 'lars@heucke.net', 'lars@heucke.net', 'ext-10200', NULL);
INSERT INTO cwd_user (id, directory_id, user_name, lower_user_name, active, created_date, updated_date, first_name, lower_first_name, last_name, lower_last_name, display_name, lower_display_name, email_address, lower_email_address, external_id, credential)
VALUES (10201, 1, 'anna.schmidt', 'anna.schmidt', 1, TIMESTAMP '2021-03-02 10:30:00', TIMESTAMP '2021-03-02 10:30:00', 'Anna', 'anna', 'Schmidt', 'schmidt', 'Anna Schmidt', 'anna schmidt', 'anna.schmidt@example.com', 'anna.schmidt@example.com', 'ext-10201', NULL);
INSERT INTO cwd_user (id, directory_id, user_name, lower_user_name, active, created_date, updated_date, first_name, lower_first_name, last_name, lower_last_name, display_name, lower_display_name, email_address, lower_email_address, external_id, credential)
VALUES (10202, 1, 'tom.weber', 'tom.weber', 1, TIMESTAMP '2021-06-20 14:00:00', TIMESTAMP '2021-06-20 14:00:00', 'Tom', 'tom', 'Weber', 'weber', 'Tom Weber', 'tom weber', 'tom.weber@example.com', 'tom.weber@example.com', 'ext-10202', NULL);
INSERT INTO cwd_user (id, directory_id, user_name, lower_user_name, active, created_date, updated_date, first_name, lower_first_name, last_name, lower_last_name, display_name, lower_display_name, email_address, lower_email_address, external_id, credential)
VALUES (10203, 1, 'maria.lopez', 'maria.lopez', 1, TIMESTAMP '2022-09-01 08:15:00', TIMESTAMP '2022-09-01 08:15:00', 'Maria', 'maria', 'Lopez', 'lopez', 'Maria Lopez', 'maria lopez', 'maria.lopez@example.com', 'maria.lopez@example.com', 'ext-10203', NULL);

-- ---------------------------------------------------------------- projects
INSERT INTO project (id, pname, url, lead, description, pkey, pcounter, assigneetype, avatar, originalkey, projecttype)
VALUES (10000, 'Demo Projekt', NULL, 'lars', 'Demonstrationsprojekt fuer den Jira-Clone.', 'DEMO', 13, 2, NULL, 'DEMO', 'software');
INSERT INTO project (id, pname, url, lead, description, pkey, pcounter, assigneetype, avatar, originalkey, projecttype)
VALUES (10001, 'Website Relaunch', NULL, 'anna.schmidt', 'Neugestaltung der Unternehmens-Website.', 'WEB', 6, 2, NULL, 'WEB', 'software');
INSERT INTO project (id, pname, url, lead, description, pkey, pcounter, assigneetype, avatar, originalkey, projecttype)
VALUES (10002, 'IT Operations', NULL, 'tom.weber', 'Betrieb und Wartung der IT-Infrastruktur.', 'OPS', 4, 2, NULL, 'OPS', 'service_desk');

-- ---------------------------------------------------------------- workflow
INSERT INTO workflowscheme (id, name, description) VALUES (10300, 'Classic Workflow Scheme', NULL);
INSERT INTO workflowschemeentity (id, scheme, workflow, issuetype) VALUES (10301, 10300, 'Classic Workflow', '0');
INSERT INTO nodeassociation (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type, sequence) VALUES (10000, 'Project', 10300, 'WorkflowScheme', 'ProjectScheme', NULL);
INSERT INTO nodeassociation (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type, sequence) VALUES (10001, 'Project', 10300, 'WorkflowScheme', 'ProjectScheme', NULL);
INSERT INTO nodeassociation (source_node_id, source_node_entity, sink_node_id, sink_node_entity, association_type, sequence) VALUES (10002, 'Project', 10300, 'WorkflowScheme', 'ProjectScheme', NULL);

INSERT INTO jiraworkflows (id, workflowname, creatorname, descriptor, islocked) VALUES (10400, 'Classic Workflow', 'lars',
'<workflow>
  <initial-actions>
    <action id="1" name="Create Issue">
      <results>
        <unconditional-result old-status="Finished" status="Open" step="1"/>
      </results>
    </action>
  </initial-actions>
  <common-actions>
    <action id="2" name="Close Issue">
      <results>
        <unconditional-result old-status="Finished" status="Closed" step="6"/>
      </results>
    </action>
    <action id="5" name="Resolve Issue">
      <results>
        <unconditional-result old-status="Finished" status="Resolved" step="4"/>
      </results>
    </action>
  </common-actions>
  <steps>
    <step id="1" name="Open">
      <meta name="jira.status.id">1</meta>
      <actions>
        <common-action id="2"/>
        <common-action id="5"/>
        <action id="4" name="Start Progress">
          <results>
            <unconditional-result old-status="Finished" status="In Progress" step="3"/>
          </results>
        </action>
      </actions>
    </step>
    <step id="3" name="In Progress">
      <meta name="jira.status.id">3</meta>
      <actions>
        <common-action id="2"/>
        <common-action id="5"/>
        <action id="301" name="Stop Progress">
          <results>
            <unconditional-result old-status="Finished" status="Open" step="1"/>
          </results>
        </action>
      </actions>
    </step>
    <step id="4" name="Resolved">
      <meta name="jira.status.id">5</meta>
      <actions>
        <common-action id="2"/>
        <action id="3" name="Reopen Issue">
          <results>
            <unconditional-result old-status="Finished" status="Open" step="1"/>
          </results>
        </action>
      </actions>
    </step>
    <step id="6" name="Closed">
      <meta name="jira.status.id">6</meta>
      <actions>
        <action id="3" name="Reopen Issue">
          <results>
            <unconditional-result old-status="Finished" status="Open" step="1"/>
          </results>
        </action>
      </actions>
    </step>
  </steps>
</workflow>', 'N');

-- ---------------------------------------------------------------- issues: DEMO (project 10000, ids 20001-20012)
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20001, 1, 10000, 'anna.schmidt', 'lars', 'anna.schmidt', '1', 'Login-Seite zeigt Fehler 500 bei falschem Passwort', 'Beim Login mit einem falschen Passwort erscheint statt einer Fehlermeldung ein HTTP-500-Fehler. Erwartet wird eine Meldung wie "Benutzername oder Passwort falsch".

Screenshot des Fehlers: !screenshot-fehler.svg!', '2', NULL, '1', TIMESTAMP '2026-05-02 09:12:00', TIMESTAMP '2026-06-10 16:40:00', NULL, NULL, 3, 5, 30001);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20002, 2, 10000, 'lars', 'tom.weber', 'lars', '3', 'Datenbank-Backup automatisieren', 'Naechtliches Backup der Oracle-Datenbank per Cronjob einrichten und Wiederherstellung dokumentieren.', '3', NULL, '3', TIMESTAMP '2026-05-03 11:00:00', TIMESTAMP '2026-06-11 09:05:00', TIMESTAMP '2026-06-30 00:00:00', NULL, 0, 2, 30002);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20003, 3, 10000, 'anna.schmidt', 'anna.schmidt', 'anna.schmidt', '10001', 'Als Nutzer moechte ich meine Vorgaenge filtern koennen', 'Filter nach Status, Typ und Volltextsuche in der Vorgangsliste.', '3', '1', '5', TIMESTAMP '2026-05-05 14:20:00', TIMESTAMP '2026-06-08 10:00:00', NULL, TIMESTAMP '2026-06-08 10:00:00', 1, 1, 30003);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20004, 4, 10000, 'tom.weber', 'lars', 'tom.weber', '1', 'Umlaute werden in Kommentaren falsch dargestellt', 'Sonderzeichen in Kommentaren erscheinen als Fragezeichen. Vermutlich falsches Encoding beim Lesen des CLOB.', '3', '1', '6', TIMESTAMP '2026-04-20 08:45:00', TIMESTAMP '2026-05-15 17:30:00', NULL, TIMESTAMP '2026-05-15 17:30:00', 0, 1, 30004);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20005, 5, 10000, 'lars', NULL, 'lars', '10001', 'Als Nutzer moechte ich Kommentare zu einem Vorgang sehen', 'Kommentar-Thread in der Detailansicht, sortiert nach Erstellungsdatum.', '4', NULL, '1', TIMESTAMP '2026-05-10 10:10:00', TIMESTAMP '2026-06-01 12:00:00', NULL, NULL, 0, 0, 30005);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20006, 6, 10000, 'anna.schmidt', 'JIRAUSER10103', 'anna.schmidt', '3', 'REST-API fuer Projektliste dokumentieren', 'OpenAPI-Beschreibung der Endpunkte erstellen.', '4', NULL, '3', TIMESTAMP '2026-05-12 13:30:00', TIMESTAMP '2026-06-09 15:20:00', NULL, NULL, 0, 0, 30006);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20007, 7, 10000, 'JIRAUSER10103', 'tom.weber', 'JIRAUSER10103', '1', 'Paging in der Vorgangsliste springt auf Seite 1 zurueck', 'Nach dem Aendern eines Filters wird die aktuelle Seite nicht zurueckgesetzt, dadurch erscheint eine leere Liste.', '4', NULL, '1', TIMESTAMP '2026-05-18 09:00:00', TIMESTAMP '2026-06-07 11:45:00', NULL, NULL, 0, 1, 30007);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20008, 8, 10000, 'lars', 'lars', 'lars', '10000', 'Migration auf neue Infrastruktur', 'Epic: Umzug aller Dienste auf die neue On-Premise-Plattform.', '2', NULL, '3', TIMESTAMP '2026-04-01 08:00:00', TIMESTAMP '2026-06-11 08:30:00', TIMESTAMP '2026-09-30 00:00:00', NULL, 2, 4, 30008);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20009, 9, 10000, 'tom.weber', 'anna.schmidt', 'tom.weber', '3', 'Monitoring-Dashboard aufsetzen', 'Grafana-Dashboard fuer die wichtigsten Kennzahlen.', '4', '1', '5', TIMESTAMP '2026-04-15 10:00:00', TIMESTAMP '2026-05-28 14:10:00', NULL, TIMESTAMP '2026-05-28 14:10:00', 0, 0, 30009);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20010, 10, 10000, 'anna.schmidt', 'lars', 'anna.schmidt', '1', 'Session laeuft beim Tippen eines Kommentars ab', 'Die Session-Timeout-Behandlung verwirft eingegebenen Text ohne Warnung.', '3', NULL, '3', TIMESTAMP '2026-05-22 16:00:00', TIMESTAMP '2026-06-10 09:55:00', NULL, NULL, 1, 2, 30010);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20011, 11, 10000, 'JIRAUSER10103', NULL, 'JIRAUSER10103', '10001', 'Dark Mode fuer die Oberflaeche', 'Wurde diskutiert und vorerst verworfen.', '5', '2', '6', TIMESTAMP '2026-03-10 12:00:00', TIMESTAMP '2026-04-02 10:00:00', NULL, TIMESTAMP '2026-04-02 10:00:00', 4, 3, 30011);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20012, 12, 10000, 'lars', 'JIRAUSER10103', 'lars', '3', 'Read-only-Datenbankbenutzer fuer Produktion anlegen', 'Oracle-Account mit reinen SELECT-Grants auf das Jira-Schema einrichten.', '2', NULL, '1', TIMESTAMP '2026-06-01 09:30:00', TIMESTAMP '2026-06-11 17:00:00', TIMESTAMP '2026-06-20 00:00:00', NULL, 0, 1, 30012);

-- ---------------------------------------------------------------- issues: WEB (project 10001, ids 20013-20018)
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20013, 1, 10001, 'anna.schmidt', 'JIRAUSER10103', 'anna.schmidt', '10000', 'Relaunch der Startseite', 'Epic: Neue Startseite mit aktualisiertem Corporate Design.', '2', NULL, '3', TIMESTAMP '2026-04-05 09:00:00', TIMESTAMP '2026-06-09 13:00:00', TIMESTAMP '2026-08-31 00:00:00', NULL, 1, 3, 30013);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20014, 2, 10001, 'JIRAUSER10103', 'anna.schmidt', 'JIRAUSER10103', '10001', 'Responsive Navigation umsetzen', 'Hamburger-Menue fuer mobile Geraete.', '3', NULL, '3', TIMESTAMP '2026-04-20 10:30:00', TIMESTAMP '2026-06-08 11:20:00', NULL, NULL, 0, 1, 30014);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20015, 3, 10001, 'anna.schmidt', 'lars', 'anna.schmidt', '1', 'Kontaktformular sendet keine E-Mails', 'SMTP-Konfiguration prueft das Zertifikat nicht korrekt.', '1', NULL, '1', TIMESTAMP '2026-06-02 08:20:00', TIMESTAMP '2026-06-11 10:10:00', NULL, NULL, 2, 4, 30015);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20016, 4, 10001, 'lars', 'JIRAUSER10103', 'lars', '3', 'Bildkomprimierung in die Build-Pipeline integrieren', 'WebP-Konvertierung beim Deployment.', '4', '1', '5', TIMESTAMP '2026-05-01 14:00:00', TIMESTAMP '2026-05-30 09:40:00', NULL, TIMESTAMP '2026-05-30 09:40:00', 0, 0, 30016);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20017, 5, 10001, 'JIRAUSER10103', NULL, 'JIRAUSER10103', '10001', 'Mehrsprachigkeit (DE/EN) vorbereiten', 'i18n-Konzept fuer Inhalte und Navigation.', '4', NULL, '1', TIMESTAMP '2026-05-15 11:00:00', TIMESTAMP '2026-06-05 16:30:00', NULL, NULL, 0, 2, 30017);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20018, 6, 10001, 'anna.schmidt', 'anna.schmidt', 'anna.schmidt', '1', 'Footer-Links fuehren auf 404-Seiten', 'Impressum und Datenschutz verlinken auf alte URLs.', '4', '1', '6', TIMESTAMP '2026-04-10 09:15:00', TIMESTAMP '2026-04-25 12:00:00', NULL, TIMESTAMP '2026-04-25 12:00:00', 0, 0, 30018);

-- ---------------------------------------------------------------- issues: OPS (project 10002, ids 20019-20022)
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20019, 1, 10002, 'tom.weber', 'tom.weber', 'tom.weber', '3', 'Zertifikate fuer interne Dienste erneuern', 'TLS-Zertifikate laufen Ende Juni ab.', '1', NULL, '3', TIMESTAMP '2026-06-01 07:45:00', TIMESTAMP '2026-06-11 08:00:00', TIMESTAMP '2026-06-25 00:00:00', NULL, 0, 2, 30019);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20020, 2, 10002, 'lars', 'tom.weber', 'lars', '1', 'VPN-Verbindung bricht nach 30 Minuten ab', 'Keepalive-Einstellungen des VPN-Gateways pruefen.', '2', NULL, '1', TIMESTAMP '2026-05-28 13:10:00', TIMESTAMP '2026-06-10 14:25:00', NULL, NULL, 1, 3, 30020);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20021, 3, 10002, 'tom.weber', 'JIRAUSER10103', 'tom.weber', '3', 'Patch-Management fuer Linux-Server dokumentieren', 'Ablauf und Wartungsfenster beschreiben.', '4', '1', '5', TIMESTAMP '2026-04-22 10:00:00', TIMESTAMP '2026-05-20 15:00:00', NULL, TIMESTAMP '2026-05-20 15:00:00', 0, 0, 30021);
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20022, 4, 10002, 'JIRAUSER10103', NULL, 'JIRAUSER10103', '3', 'Alte Fileserver-Freigaben archivieren', 'Nicht mehr genutzte Shares identifizieren und archivieren.', '5', NULL, '1', TIMESTAMP '2026-05-30 09:00:00', TIMESTAMP '2026-06-03 11:30:00', NULL, NULL, 0, 0, 30022);

-- DEMO-13: Initiative oberhalb des Epics DEMO-8
INSERT INTO jiraissue (id, issuenum, project, reporter, assignee, creator, issuetype, summary, description, priority, resolution, issuestatus, created, updated, duedate, resolutiondate, votes, watches, workflow_id)
VALUES (20023, 13, 10000, 'lars', 'lars', 'lars', '10200', 'Modernisierung der IT-Landschaft', 'Initiative: Buendelt alle Epics zur Modernisierung von Infrastruktur und Anwendungen.', '2', NULL, '3', TIMESTAMP '2026-03-01 08:00:00', TIMESTAMP '2026-06-11 07:50:00', TIMESTAMP '2026-12-31 00:00:00', NULL, 0, 5, 30023);

-- ---------------------------------------------------------------- hierarchy: link types, links, custom fields
INSERT INTO issuelinktype (id, linkname, inward, outward, pstyle) VALUES (10500, 'Epic-Story Link', 'has Epic', 'is Epic of', 'jira_gh_epic_story');
INSERT INTO issuelinktype (id, linkname, inward, outward, pstyle) VALUES (10501, 'jira_subtask_link', 'jira_subtask_inward', 'jira_subtask_outward', 'jira_subtask');
INSERT INTO issuelinktype (id, linkname, inward, outward, pstyle) VALUES (10502, 'Relates', 'relates to', 'relates to', NULL);

-- Epic DEMO-8 -> Stories/Tasks (per Epic-Story-Link)
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10600, 10500, 20008, 20002, NULL);
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10601, 10500, 20008, 20009, NULL);
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10602, 10500, 20008, 20012, NULL);
-- Epic WEB-1 -> Stories/Tasks
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10603, 10500, 20013, 20014, NULL);
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10604, 10500, 20013, 20016, NULL);
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10605, 10500, 20013, 20017, NULL);
-- Nicht-hierarchischer Link (darf NICHT als Kind erscheinen)
INSERT INTO issuelink (id, linktype, source, destination, sequence) VALUES (10606, 10502, 20001, 20002, NULL);

-- Custom Fields: Advanced-Roadmaps Parent Link (Initiative->Epic) und Epic Link
INSERT INTO customfield (id, customfieldtypekey, cfname) VALUES (10700, 'com.atlassian.jpo:jpo-custom-field-parent', 'Parent Link');
INSERT INTO customfield (id, customfieldtypekey, cfname) VALUES (10701, 'com.pyxis.greenhopper.jira:gh-epic-link', 'Epic Link');
-- DEMO-8 haengt unter Initiative DEMO-13 (Parent Link speichert die Issue-ID als String)
INSERT INTO customfieldvalue (id, issue, customfield, stringvalue, numbervalue, textvalue, datevalue, valuetype) VALUES (10800, 20008, 10700, '20023', NULL, NULL, NULL, NULL);
-- DEMO-3 haengt per Epic-Link-Custom-Field unter Epic DEMO-8 (Epic Link speichert die Issue-ID als Zahl)
INSERT INTO customfieldvalue (id, issue, customfield, stringvalue, numbervalue, textvalue, datevalue, valuetype) VALUES (10801, 20003, 10701, NULL, 20008, NULL, NULL, NULL);

-- ---------------------------------------------------------------- attachments (Dateien liegen unter dev-attachments/)
INSERT INTO fileattachment (id, issueid, mimetype, filename, created, filesize, author, zip, thumbnailable) VALUES (70001, 20001, 'image/svg+xml', 'screenshot-fehler.svg', TIMESTAMP '2026-05-02 09:15:00', 760, 'anna.schmidt', 0, 1);
INSERT INTO fileattachment (id, issueid, mimetype, filename, created, filesize, author, zip, thumbnailable) VALUES (70002, 20001, 'text/plain', 'stacktrace.log', TIMESTAMP '2026-05-02 10:06:00', 230, 'lars', 0, 0);

-- ---------------------------------------------------------------- workflow state per issue (step matches issuestatus)
-- status 1 (Open) -> step 1, status 3 (In Progress) -> step 3, status 5 (Resolved) -> step 4, status 6 (Closed) -> step 6
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40001, 30001, 1, 0, NULL, TIMESTAMP '2026-05-02 09:12:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40002, 30002, 3, 0, NULL, TIMESTAMP '2026-05-03 11:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40003, 30003, 4, 0, NULL, TIMESTAMP '2026-05-05 14:20:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40004, 30004, 6, 0, NULL, TIMESTAMP '2026-04-20 08:45:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40005, 30005, 1, 0, NULL, TIMESTAMP '2026-05-10 10:10:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40006, 30006, 3, 0, NULL, TIMESTAMP '2026-05-12 13:30:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40007, 30007, 1, 0, NULL, TIMESTAMP '2026-05-18 09:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40008, 30008, 3, 0, NULL, TIMESTAMP '2026-04-01 08:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40009, 30009, 4, 0, NULL, TIMESTAMP '2026-04-15 10:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40010, 30010, 3, 0, NULL, TIMESTAMP '2026-05-22 16:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40011, 30011, 6, 0, NULL, TIMESTAMP '2026-03-10 12:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40012, 30012, 1, 0, NULL, TIMESTAMP '2026-06-01 09:30:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40013, 30013, 3, 0, NULL, TIMESTAMP '2026-04-05 09:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40014, 30014, 3, 0, NULL, TIMESTAMP '2026-04-20 10:30:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40015, 30015, 1, 0, NULL, TIMESTAMP '2026-06-02 08:20:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40016, 30016, 4, 0, NULL, TIMESTAMP '2026-05-01 14:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40017, 30017, 1, 0, NULL, TIMESTAMP '2026-05-15 11:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40018, 30018, 6, 0, NULL, TIMESTAMP '2026-04-10 09:15:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40019, 30019, 3, 0, NULL, TIMESTAMP '2026-06-01 07:45:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40020, 30020, 1, 0, NULL, TIMESTAMP '2026-05-28 13:10:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40021, 30021, 4, 0, NULL, TIMESTAMP '2026-04-22 10:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40022, 30022, 1, 0, NULL, TIMESTAMP '2026-05-30 09:00:00', NULL, NULL, NULL);
INSERT INTO os_currentstep (id, entry_id, step_id, action_id, owner, start_date, due_date, finish_date, status) VALUES (40023, 30023, 3, 0, NULL, TIMESTAMP '2026-03-01 08:00:00', NULL, NULL, NULL);

-- ---------------------------------------------------------------- comments
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50001, 20001, 'lars', 'comment', NULL, NULL, 'Kann ich reproduzieren. Der Stacktrace zeigt eine NullPointerException im AuthFilter.', TIMESTAMP '2026-05-02 10:05:00', 'lars', TIMESTAMP '2026-05-02 10:05:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50002, 20001, 'anna.schmidt', 'comment', NULL, NULL, 'Tritt nur auf, wenn der Benutzername Grossbuchstaben enthaelt.', TIMESTAMP '2026-05-03 08:30:00', 'anna.schmidt', TIMESTAMP '2026-05-03 08:30:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50003, 20001, 'tom.weber', 'comment', NULL, NULL, 'Workaround: Benutzernamen vor dem Lookup in Kleinbuchstaben umwandeln.', TIMESTAMP '2026-06-10 16:40:00', 'tom.weber', TIMESTAMP '2026-06-10 16:40:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50004, 20002, 'tom.weber', 'comment', NULL, NULL, 'RMAN-Skript liegt im Ops-Repo, Testlauf am Wochenende geplant.', TIMESTAMP '2026-06-11 09:05:00', 'tom.weber', TIMESTAMP '2026-06-11 09:05:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50005, 20003, 'lars', 'comment', NULL, NULL, 'Umgesetzt und auf der Testumgebung verifiziert.', TIMESTAMP '2026-06-08 10:00:00', 'lars', TIMESTAMP '2026-06-08 10:00:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50006, 20015, 'lars', 'comment', NULL, NULL, 'Zertifikatskette des SMTP-Servers ist unvollstaendig, Intermediate fehlt.', TIMESTAMP '2026-06-11 10:10:00', 'lars', TIMESTAMP '2026-06-11 10:10:00', NULL);
INSERT INTO jiraaction (id, issueid, author, actiontype, actionlevel, rolelevel, actionbody, created, updateauthor, updated, actionnum)
VALUES (50007, 20019, 'tom.weber', 'comment', NULL, NULL, 'Neue Zertifikate sind beantragt, Austausch im Wartungsfenster Donnerstag.', TIMESTAMP '2026-06-11 08:00:00', 'tom.weber', TIMESTAMP '2026-06-11 08:00:00', NULL);

-- ---------------------------------------------------------------- labels
INSERT INTO label (id, fieldid, issue, label) VALUES (60001, NULL, 20001, 'login');
INSERT INTO label (id, fieldid, issue, label) VALUES (60002, NULL, 20001, 'security');
INSERT INTO label (id, fieldid, issue, label) VALUES (60003, NULL, 20002, 'datenbank');
INSERT INTO label (id, fieldid, issue, label) VALUES (60004, NULL, 20008, 'infrastruktur');
INSERT INTO label (id, fieldid, issue, label) VALUES (60005, NULL, 20008, 'migration');
INSERT INTO label (id, fieldid, issue, label) VALUES (60006, NULL, 20015, 'smtp');
INSERT INTO label (id, fieldid, issue, label) VALUES (60007, NULL, 20019, 'tls');
