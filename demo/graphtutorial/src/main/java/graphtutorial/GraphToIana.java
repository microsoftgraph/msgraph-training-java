// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
/* spell-checker: disable */

// <zoneMappingsSnippet>
package graphtutorial;

import java.time.ZoneId;
import java.util.Map;

// Basic lookup for mapping Windows time zone identifiers to
// IANA identifiers
// Mappings taken from
// https://github.com/unicode-org/cldr/blob/master/common/supplemental/windowsZones.xml
public class GraphToIana {
    private static final Map<String, String> timeZoneIdMap =
      Map.ofEntries(
        Map.entry("Dateline Standard Time", "Etc/GMT+12"),
        Map.entry("UTC-11", "Etc/GMT+11"),
        Map.entry("Aleutian Standard Time", "America/Adak"),
        Map.entry("Hawaiian Standard Time", "Pacific/Honolulu"),
        Map.entry("Marquesas Standard Time", "Pacific/Marquesas"),
        Map.entry("Alaskan Standard Time", "America/Anchorage"),
        Map.entry("UTC-09", "Etc/GMT+9"),
        Map.entry("Pacific Standard Time (Mexico)", "America/Tijuana"),
        Map.entry("UTC-08", "Etc/GMT+8"),
        Map.entry("Pacific Standard Time", "America/Los_Angeles"),
        Map.entry("US Mountain Standard Time", "America/Phoenix"),
        Map.entry("Mountain Standard Time (Mexico)", "America/Chihuahua"),
        Map.entry("Mountain Standard Time", "America/Denver"),
        Map.entry("Central America Standard Time", "America/Guatemala"),
        Map.entry("Central Standard Time", "America/Chicago"),
        Map.entry("Easter Island Standard Time", "Pacific/Easter"),
        Map.entry("Central Standard Time (Mexico)", "America/Mexico_City"),
        Map.entry("Canada Central Standard Time", "America/Regina"),
        Map.entry("SA Pacific Standard Time", "America/Bogota"),
        Map.entry("Eastern Standard Time (Mexico)", "America/Cancun"),
        Map.entry("Eastern Standard Time", "America/New_York"),
        Map.entry("Haiti Standard Time", "America/Port-au-Prince"),
        Map.entry("Cuba Standard Time", "America/Havana"),
        Map.entry("US Eastern Standard Time", "America/Indianapolis"),
        Map.entry("Turks And Caicos Standard Time", "America/Grand_Turk"),
        Map.entry("Paraguay Standard Time", "America/Asuncion"),
        Map.entry("Atlantic Standard Time", "America/Halifax"),
        Map.entry("Venezuela Standard Time", "America/Caracas"),
        Map.entry("Central Brazilian Standard Time", "America/Cuiaba"),
        Map.entry("SA Western Standard Time", "America/La_Paz"),
        Map.entry("Pacific SA Standard Time", "America/Santiago"),
        Map.entry("Newfoundland Standard Time", "America/St_Johns"),
        Map.entry("Tocantins Standard Time", "America/Araguaina"),
        Map.entry("E. South America Standard Time", "America/Sao_Paulo"),
        Map.entry("SA Eastern Standard Time", "America/Cayenne"),
        Map.entry("Argentina Standard Time", "America/Buenos_Aires"),
        Map.entry("Greenland Standard Time", "America/Godthab"),
        Map.entry("Montevideo Standard Time", "America/Montevideo"),
        Map.entry("Magallanes Standard Time", "America/Punta_Arenas"),
        Map.entry("Saint Pierre Standard Time", "America/Miquelon"),
        Map.entry("Bahia Standard Time", "America/Bahia"),
        Map.entry("UTC-02", "Etc/GMT+2"),
        Map.entry("Azores Standard Time", "Atlantic/Azores"),
        Map.entry("Cape Verde Standard Time", "Atlantic/Cape_Verde"),
        Map.entry("UTC", "Etc/GMT"),
        Map.entry("GMT Standard Time", "Europe/London"),
        Map.entry("Greenwich Standard Time", "Atlantic/Reykjavik"),
        Map.entry("Sao Tome Standard Time", "Africa/Sao_Tome"),
        Map.entry("Morocco Standard Time", "Africa/Casablanca"),
        Map.entry("W. Europe Standard Time", "Europe/Berlin"),
        Map.entry("Central Europe Standard Time", "Europe/Budapest"),
        Map.entry("Romance Standard Time", "Europe/Paris"),
        Map.entry("Central European Standard Time", "Europe/Warsaw"),
        Map.entry("W. Central Africa Standard Time", "Africa/Lagos"),
        Map.entry("Jordan Standard Time", "Asia/Amman"),
        Map.entry("GTB Standard Time", "Europe/Bucharest"),
        Map.entry("Middle East Standard Time", "Asia/Beirut"),
        Map.entry("Egypt Standard Time", "Africa/Cairo"),
        Map.entry("E. Europe Standard Time", "Europe/Chisinau"),
        Map.entry("Syria Standard Time", "Asia/Damascus"),
        Map.entry("West Bank Standard Time", "Asia/Hebron"),
        Map.entry("South Africa Standard Time", "Africa/Johannesburg"),
        Map.entry("FLE Standard Time", "Europe/Kiev"),
        Map.entry("Israel Standard Time", "Asia/Jerusalem"),
        Map.entry("Kaliningrad Standard Time", "Europe/Kaliningrad"),
        Map.entry("Sudan Standard Time", "Africa/Khartoum"),
        Map.entry("Libya Standard Time", "Africa/Tripoli"),
        Map.entry("Namibia Standard Time", "Africa/Windhoek"),
        Map.entry("Arabic Standard Time", "Asia/Baghdad"),
        Map.entry("Turkey Standard Time", "Europe/Istanbul"),
        Map.entry("Arab Standard Time", "Asia/Riyadh"),
        Map.entry("Belarus Standard Time", "Europe/Minsk"),
        Map.entry("Russian Standard Time", "Europe/Moscow"),
        Map.entry("E. Africa Standard Time", "Africa/Nairobi"),
        Map.entry("Iran Standard Time", "Asia/Tehran"),
        Map.entry("Arabian Standard Time", "Asia/Dubai"),
        Map.entry("Astrakhan Standard Time", "Europe/Astrakhan"),
        Map.entry("Azerbaijan Standard Time", "Asia/Baku"),
        Map.entry("Russia Time Zone 3", "Europe/Samara"),
        Map.entry("Mauritius Standard Time", "Indian/Mauritius"),
        Map.entry("Saratov Standard Time", "Europe/Saratov"),
        Map.entry("Georgian Standard Time", "Asia/Tbilisi"),
        Map.entry("Volgograd Standard Time", "Europe/Volgograd"),
        Map.entry("Caucasus Standard Time", "Asia/Yerevan"),
        Map.entry("Afghanistan Standard Time", "Asia/Kabul"),
        Map.entry("West Asia Standard Time", "Asia/Tashkent"),
        Map.entry("Ekaterinburg Standard Time", "Asia/Yekaterinburg"),
        Map.entry("Pakistan Standard Time", "Asia/Karachi"),
        Map.entry("Qyzylorda Standard Time", "Asia/Qyzylorda"),
        Map.entry("India Standard Time", "Asia/Calcutta"),
        Map.entry("Sri Lanka Standard Time", "Asia/Colombo"),
        Map.entry("Nepal Standard Time", "Asia/Katmandu"),
        Map.entry("Central Asia Standard Time", "Asia/Almaty"),
        Map.entry("Bangladesh Standard Time", "Asia/Dhaka"),
        Map.entry("Omsk Standard Time", "Asia/Omsk"),
        Map.entry("Myanmar Standard Time", "Asia/Rangoon"),
        Map.entry("SE Asia Standard Time", "Asia/Bangkok"),
        Map.entry("Altai Standard Time", "Asia/Barnaul"),
        Map.entry("W. Mongolia Standard Time", "Asia/Hovd"),
        Map.entry("North Asia Standard Time", "Asia/Krasnoyarsk"),
        Map.entry("N. Central Asia Standard Time", "Asia/Novosibirsk"),
        Map.entry("Tomsk Standard Time", "Asia/Tomsk"),
        Map.entry("China Standard Time", "Asia/Shanghai"),
        Map.entry("North Asia East Standard Time", "Asia/Irkutsk"),
        Map.entry("Singapore Standard Time", "Asia/Singapore"),
        Map.entry("W. Australia Standard Time", "Australia/Perth"),
        Map.entry("Taipei Standard Time", "Asia/Taipei"),
        Map.entry("Ulaanbaatar Standard Time", "Asia/Ulaanbaatar"),
        Map.entry("Aus Central W. Standard Time", "Australia/Eucla"),
        Map.entry("Transbaikal Standard Time", "Asia/Chita"),
        Map.entry("Tokyo Standard Time", "Asia/Tokyo"),
        Map.entry("North Korea Standard Time", "Asia/Pyongyang"),
        Map.entry("Korea Standard Time", "Asia/Seoul"),
        Map.entry("Yakutsk Standard Time", "Asia/Yakutsk"),
        Map.entry("Cen. Australia Standard Time", "Australia/Adelaide"),
        Map.entry("AUS Central Standard Time", "Australia/Darwin"),
        Map.entry("E. Australia Standard Time", "Australia/Brisbane"),
        Map.entry("AUS Eastern Standard Time", "Australia/Sydney"),
        Map.entry("West Pacific Standard Time", "Pacific/Port_Moresby"),
        Map.entry("Tasmania Standard Time", "Australia/Hobart"),
        Map.entry("Vladivostok Standard Time", "Asia/Vladivostok"),
        Map.entry("Lord Howe Standard Time", "Australia/Lord_Howe"),
        Map.entry("Bougainville Standard Time", "Pacific/Bougainville"),
        Map.entry("Russia Time Zone 10", "Asia/Srednekolymsk"),
        Map.entry("Magadan Standard Time", "Asia/Magadan"),
        Map.entry("Norfolk Standard Time", "Pacific/Norfolk"),
        Map.entry("Sakhalin Standard Time", "Asia/Sakhalin"),
        Map.entry("Central Pacific Standard Time", "Pacific/Guadalcanal"),
        Map.entry("Russia Time Zone 11", "Asia/Kamchatka"),
        Map.entry("New Zealand Standard Time", "Pacific/Auckland"),
        Map.entry("UTC+12", "Etc/GMT-12"),
        Map.entry("Fiji Standard Time", "Pacific/Fiji"),
        Map.entry("Chatham Islands Standard Time", "Pacific/Chatham"),
        Map.entry("UTC+13", "Etc/GMT-13"),
        Map.entry("Tonga Standard Time", "Pacific/Tongatapu"),
        Map.entry("Samoa Standard Time", "Pacific/Apia"),
        Map.entry("Line Islands Standard Time", "Pacific/Kiritimati")
      );

    public static String getIanaFromWindows(String windowsTimeZone) {
        String iana = timeZoneIdMap.get(windowsTimeZone);

        // If a mapping was not found, assume the value passed
        // was already an IANA identifier
        return (iana == null) ? windowsTimeZone : iana;
    }

    public static ZoneId getZoneIdFromWindows(String windowsTimeZone) {
        String timeZoneId = getIanaFromWindows(windowsTimeZone);

        return ZoneId.of(timeZoneId);
    }
}
// </zoneMappingsSnippet>
