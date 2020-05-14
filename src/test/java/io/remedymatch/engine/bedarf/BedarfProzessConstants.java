package io.remedymatch.engine.bedarf;

public class BedarfProzessConstants {

    public static final String PROZESS_KEY = "bedarf_prozess";

    public static final String BUSINESS_KEY = "businessKey";

    public static final String START_BEDARF_ERHALTEN = "bedarf_prozess_bedarf_erhalten";
    public static final String END_LOGISTIKAUFTRAG_ERSTELLT = "bedarf_prozess_logistikauftrag_erstellt";
    public static final String END_ANFRAGE_ABGELEHNT = "bedarf_prozess_anfrage_abgelehnt";
    public static final String END_BENACHRICHTIGUNG_EINGESTELLT = "bedarf_prozess_ende_benachrichtigung_eingestellt";
    public static final String END_REST_BEDARF_GEAENDERT = "bedarf_prozess_ende_rest_geaendert";
    public static final String END_ANFRAGE_STORNIERT = "bedarf_prozess_ende_anfrage_storniert";

    public static final String TASK_STORNIERUNG_ERHALTEN = "bedarf_prozess_stornierung_erhalten";
    public static final String TASK_BEDARF_SCHLIESSEN_STORNIERUNG = "bedarf_prozess_bedarf_schliessen_stornierung";
    public static final String TASK_BEDARF_SCHLIESSEN_ANZAHL = "bedarf_prozess_bedarf_schliessen_anzahl";
    public static final String TASK_ANFRAGE_BEARBEITEN = "bedarf_prozess_anfrage_bearbeiten";
    public static final String TASK_MATCH_ANLEGEN = "bedarf_prozess_match_anlegen";
    public static final String TASK_LOGISTIKAUFTRAG_ERSTELLEN = "bedarf_prozess_logistikauftrag_erstellen";
    public static final String TASK_ANFRAGE_ABLEHNEN = "bedarf_prozess_anfrage_ablehnen";
    public static final String TASK_BENACHRICHTUNG_EINSTELLEN = "bedarf_prozess_benachrichtigung_einstellen";
    public static final String TASK_ANFRAGE_STORNIEREN = "bedarf_prozess_anfrage_stornieren";

    public static final String SUBPROZESS_ANFRAGE = "bedarf_anfrage_subprozess";

    public static final String MESSAGE_BEDARF_GESCHLOSSEN = "bedarf_prozess_geschlossen_message";
    public static final String MESSAGE_ANFRAGE_ERHALTEN = "bedarf_prozess_anfrage_erhalten_message";
    public static final String MESSAGE_REST_BEDARF_GEAENDERT = "bedarf_prozess_rest_geaendert_message";
    public static final String MESSAGE_BEDARF_UNGUELTIG = "bedarf_prozess_bedarf_ungueltig_message";
    public static final String MESSAGE_ANFRAGE_STORNIEREN = "bedarf_prozess_anfrage_storniert_message";
    public static final String MESSAGE_ANFRAGE_BEARBEITET = "bedarf_prozess_anfrage_bearbeitet_message";

    public static final String TOPIC_BEDARF_SCHLIESSEN = "bedarf_schliessen_topic";
    public static final String TOPIC_MATCH_ANLEGEN = "bedarf_anfrage_match_anlegen_topic";
    public static final String TOPIC_LOGISTIKAUFTRAG_ERSTELLEN = "bedarf_anfrage_logistik_erstellen_topic";
    public static final String TOPIC_ANFRAGE_ABLEHNEN = "bedarf_anfrage_ablehnen_topic";
    public static final String TOPIC_ANFRAGE_SCHLIESSEN = "bedarf_anfrage_schliessen_topic";
    public static final String TOPIC_ANFRAGE_STORNIEREN = "bedarf_anfrage_stornieren_topic";
    public static final String TOPIC_BENACHRICHTUNG_EINSTELLEN = "bedarf_benachrichtung_einstellen_topic";

    public static final String VAR_ANZAHL = "bedarf_anzahl";
    public static final String VAR_ANFRAGE_ANGENOMMEN = "anfrage_angenommen";
    public static final String VAR_ANFRAGE_ID = "anfrage_id";
    public static final String VAR_ANFRAGE_BEDARF_ID = "anfrage_bedarf_id";

}
