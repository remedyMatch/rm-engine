package io.remedymatch.engine.angebot;

public class AngebotProzessConstants {

    public static final String PROZESS_KEY = "angebot_prozess";

    public static final String BUSINESS_KEY = "businessKey";

    public static final String START_ANGEBOT_ERHALTEN = "angebot_prozess_angebot_erhalten";
    public static final String END_LOGISTIKAUFTRAG_ERSTELLT = "angebot_prozess_logistikauftrag_erstellt";
    public static final String END_ANFRAGE_ABGELEHNT = "angebot_prozess_anfrage_abgelehnt";
    public static final String END_BENACHRICHTIGUNG_EINGESTELLT = "angebot_prozess_ende_benachrichtigung_eingestellt";
    public static final String END_REST_ANGEBOT_GEAENDERT = "angebot_prozess_ende_rest_geaendert";
    public static final String END_ANFRAGE_STORNIERT = "angebot_prozess_ende_anfrage_storniert";

    public static final String TASK_STORNIERUNG_ERHALTEN = "angebot_prozess_stornierung_erhalten";
    public static final String TASK_ANGEBOT_SCHLIESSEN_STORNIERUNG = "angebot_prozess_angebot_schliessen_stornierung";
    public static final String TASK_ANGEBOT_SCHLIESSEN_ANZAHL = "angebot_prozess_angebot_schliessen_anzahl";
    public static final String TASK_ANFRAGE_BEARBEITEN = "angebot_prozess_anfrage_bearbeiten";
    public static final String TASK_MATCH_ANLEGEN = "angebot_prozess_match_anlegen";
    public static final String TASK_LOGISTIKAUFTRAG_ERSTELLEN = "angebot_prozess_logistikauftrag_erstellen";
    public static final String TASK_ANFRAGE_ABLEHNEN = "angebot_prozess_anfrage_ablehnen";
    public static final String TASK_BENACHRICHTUNG_EINSTELLEN = "angebot_prozess_benachrichtigung_einstellen";
    public static final String TASK_ANFRAGE_STORNIEREN = "angebot_prozess_anfrage_stornieren";

    public static final String SUBPROZESS_ANFRAGE = "angebot_anfrage_subprozess";

    public static final String MESSAGE_ANGEBOT_GESCHLOSSEN = "angebot_prozess_geschlossen_message";
    public static final String MESSAGE_ANFRAGE_ERHALTEN = "angebot_prozess_anfrage_erhalten_message";
    public static final String MESSAGE_REST_ANGEBOT_GEAENDERT = "angebot_prozess_rest_geaendert_message";
    public static final String MESSAGE_ANGEBOT_UNGUELTIG = "angebot_prozess_angebot_ungueltig_message";
    public static final String MESSAGE_ANFRAGE_STORNIEREN = "angebot_prozess_anfrage_storniert_message";
    public static final String MESSAGE_ANFRAGE_BEARBEITET = "angebot_prozess_anfrage_bearbeitet_message";

    public static final String TOPIC_ANGEBOT_SCHLIESSEN = "angebot_schliessen_topic";
    public static final String TOPIC_MATCH_ANLEGEN = "angebot_anfrage_match_anlegen_topic";
    public static final String TOPIC_LOGISTIKAUFTRAG_ERSTELLEN = "angebot_anfrage_logistik_erstellen_topic";
    public static final String TOPIC_ANFRAGE_ABLEHNEN = "angebot_anfrage_ablehnen_topic";
    public static final String TOPIC_ANFRAGE_SCHLIESSEN = "angebot_anfrage_schliessen_topic";
    public static final String TOPIC_ANFRAGE_STORNIEREN = "angebot_anfrage_stornieren_topic";
    public static final String TOPIC_BENACHRICHTUNG_EINSTELLEN = "angebot_benachrichtung_einstellen_topic";

    public static final String VAR_ANZAHL = "angebot_anzahl";
    public static final String VAR_ANFRAGE_ANGENOMMEN = "anfrage_angenommen";
    public static final String VAR_ANFRAGE_ID = "angebot_anfrage_id";
}
