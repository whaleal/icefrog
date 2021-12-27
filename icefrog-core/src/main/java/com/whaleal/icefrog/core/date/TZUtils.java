package com.whaleal.icefrog.core.date;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 国家列表
 * 国家时区
 */
public class TZUtils {
    private static final Map<String, String> tzDisplayMap = new HashMap<>();
    private static final Map<String, String> tzCanonicalMap = new HashMap<>();
    private static final Map<String, Boolean> tzNoLongerSupported = new HashMap<>();

    static {

        tzDisplayMap.put("Africa/Algiers", "West Central Africa");

        tzDisplayMap.put("Africa/Cairo", "Cairo");

        tzDisplayMap.put("Africa/Casablanca", "Casablanca");

        tzDisplayMap.put("Africa/Harare", "Harare");

        tzDisplayMap.put("Africa/Johannesburg", "Pretoria");

        tzDisplayMap.put("Africa/Monrovia", "Monrovia");

        tzDisplayMap.put("Africa/Nairobi", "Nairobi");

        tzDisplayMap.put("America/Argentina/Buenos_Aires", "Buenos Aires");

        tzDisplayMap.put("America/Bogota", "Bogota");

        tzDisplayMap.put("America/Caracas", "Caracas");

        tzDisplayMap.put("America/Chicago", "Central Time (US & Canada)");

        tzDisplayMap.put("America/Chihuahua", "Chihuahua");

        tzDisplayMap.put("America/Denver", "Mountain Time (US & Canada)");

        tzDisplayMap.put("America/Godthab", "Greenland");

        tzDisplayMap.put("America/Guatemala", "Central America");

        tzDisplayMap.put("America/Guyana", "Georgetown");

        tzDisplayMap.put("America/Halifax", "Atlantic Time (Canada)");

        tzDisplayMap.put("America/Indiana/Indianapolis", "Indiana (East)");

        tzDisplayMap.put("America/Juneau", "Alaska");

        tzDisplayMap.put("America/La_Paz", "La Paz");

        tzDisplayMap.put("America/Lima", "Lima");

        tzDisplayMap.put("America/Los_Angeles", "Pacific Time (US & Canada)");

        tzDisplayMap.put("America/Mazatlan", "Mazatlan");

        tzDisplayMap.put("America/Mexico_City", "Mexico City");

        tzDisplayMap.put("America/Monterrey", "Monterrey");

        tzDisplayMap.put("America/Montevideo", "Montevideo");

        tzDisplayMap.put("America/New_York", "Eastern Time (US & Canada)");

        tzDisplayMap.put("America/Phoenix", "Arizona");

        tzDisplayMap.put("America/Regina", "Saskatchewan");

        tzDisplayMap.put("America/Santiago", "Santiago");

        tzDisplayMap.put("America/Sao_Paulo", "Brasilia");

        tzDisplayMap.put("America/St_Johns", "Newfoundland");

        tzDisplayMap.put("America/Tijuana", "Tijuana");

        tzDisplayMap.put("Asia/Almaty", "Almaty");

        tzDisplayMap.put("Asia/Baghdad", "Baghdad");

        tzDisplayMap.put("Asia/Baku", "Baku");

        tzDisplayMap.put("Asia/Bangkok", "Bangkok");

        tzDisplayMap.put("Asia/Chongqing", "Chongqing");

        tzDisplayMap.put("Asia/Colombo", "Sri Jayawardenepura");

        tzDisplayMap.put("Asia/Dhaka", "Dhaka");

        tzDisplayMap.put("Asia/Hong_Kong", "Hong Kong");

        tzDisplayMap.put("Asia/Irkutsk", "Irkutsk");

        tzDisplayMap.put("Asia/Jakarta", "Jakarta");

        tzDisplayMap.put("Asia/Jerusalem", "Jerusalem");

        tzDisplayMap.put("Asia/Kabul", "Kabul");

        tzDisplayMap.put("Asia/Kamchatka", "Kamchatka");

        tzDisplayMap.put("Asia/Karachi", "Karachi");

        tzDisplayMap.put("Asia/Kathmandu", "Kathmandu");

        tzDisplayMap.put("Asia/Kolkata", "Kolkata");

        tzDisplayMap.put("Asia/Krasnoyarsk", "Krasnoyarsk");

        tzDisplayMap.put("Asia/Kuala_Lumpur", "Kuala Lumpur");

        tzDisplayMap.put("Asia/Kuwait", "Kuwait");

        tzDisplayMap.put("Asia/Magadan", "Magadan");

        tzDisplayMap.put("Asia/Muscat", "Muscat");

        tzDisplayMap.put("Asia/Novosibirsk", "Novosibirsk");

        tzDisplayMap.put("Asia/Rangoon", "Rangoon");

        tzDisplayMap.put("Asia/Riyadh", "Riyadh");

        tzDisplayMap.put("Asia/Seoul", "Seoul");

        tzDisplayMap.put("Asia/Shanghai", "Beijing");

        tzDisplayMap.put("Asia/Singapore", "Singapore");

        tzDisplayMap.put("Asia/Taipei", "Taipei");

        tzDisplayMap.put("Asia/Tashkent", "Tashkent");

        tzDisplayMap.put("Asia/Tbilisi", "Tbilisi");

        tzDisplayMap.put("Asia/Tehran", "Tehran");

        tzDisplayMap.put("Asia/Tokyo", "Tokyo");

        tzDisplayMap.put("Asia/Ulaanbaatar", "Ulaanbaatar");

        tzDisplayMap.put("Asia/Urumqi", "Urumqi");

        tzDisplayMap.put("Asia/Vladivostok", "Vladivostok");

        tzDisplayMap.put("Asia/Yakutsk", "Yakutsk");

        tzDisplayMap.put("Asia/Yekaterinburg", "Ekaterinburg");

        tzDisplayMap.put("Asia/Yerevan", "Yerevan");

        tzDisplayMap.put("Atlantic/Azores", "Azores");

        tzDisplayMap.put("Atlantic/Cape_Verde", "Cape Verde Is.");

        tzDisplayMap.put("Atlantic/South_Georgia", "Mid-Atlantic");

        tzDisplayMap.put("Australia/Adelaide", "Adelaide");

        tzDisplayMap.put("Australia/Brisbane", "Brisbane");

        tzDisplayMap.put("Australia/Darwin", "Darwin");

        tzDisplayMap.put("Australia/Hobart", "Hobart");

        tzDisplayMap.put("Australia/Melbourne", "Melbourne");

        tzDisplayMap.put("Australia/Perth", "Perth");

        tzDisplayMap.put("Australia/Sydney", "Sydney");

        tzDisplayMap.put("Etc/UTC", "UTC");

        tzDisplayMap.put("Europe/Amsterdam", "Amsterdam");

        tzDisplayMap.put("Europe/Athens", "Athens");

        tzDisplayMap.put("Europe/Belgrade", "Belgrade");

        tzDisplayMap.put("Europe/Berlin", "Berlin");

        tzDisplayMap.put("Europe/Bratislava", "Bratislava");

        tzDisplayMap.put("Europe/Brussels", "Brussels");

        tzDisplayMap.put("Europe/Bucharest", "Bucharest");

        tzDisplayMap.put("Europe/Budapest", "Budapest");

        tzDisplayMap.put("Europe/Copenhagen", "Copenhagen");

        tzDisplayMap.put("Europe/Dublin", "Dublin");

        tzDisplayMap.put("Europe/Helsinki", "Helsinki");

        tzDisplayMap.put("Europe/Istanbul", "Istanbul");

        tzDisplayMap.put("Europe/Kiev", "Kyiv");

        tzDisplayMap.put("Europe/Lisbon", "Lisbon");

        tzDisplayMap.put("Europe/Ljubljana", "Ljubljana");

        tzDisplayMap.put("Europe/London", "London");

        tzDisplayMap.put("Europe/Madrid", "Madrid");

        tzDisplayMap.put("Europe/Minsk", "Minsk");

        tzDisplayMap.put("Europe/Moscow", "Moscow");

        tzDisplayMap.put("Europe/Paris", "Paris");

        tzDisplayMap.put("Europe/Prague", "Prague");

        tzDisplayMap.put("Europe/Riga", "Riga");

        tzDisplayMap.put("Europe/Rome", "Rome");

        tzDisplayMap.put("Europe/Sarajevo", "Sarajevo");

        tzDisplayMap.put("Europe/Skopje", "Skopje");

        tzDisplayMap.put("Europe/Sofia", "Sofia");

        tzDisplayMap.put("Europe/Stockholm", "Stockholm");

        tzDisplayMap.put("Europe/Tallinn", "Tallinn");

        tzDisplayMap.put("Europe/Vienna", "Vienna");

        tzDisplayMap.put("Europe/Vilnius", "Vilnius");

        tzDisplayMap.put("Europe/Warsaw", "Warsaw");

        tzDisplayMap.put("Europe/Zagreb", "Zagreb");

        tzDisplayMap.put("Pacific/Apia", "Samoa");

        tzDisplayMap.put("Pacific/Auckland", "Auckland");

        tzDisplayMap.put("Pacific/Chatham", "Chatham Is.");

        tzDisplayMap.put("Pacific/Fakaofo", "Tokelau Is.");

        tzDisplayMap.put("Pacific/Fiji", "Fiji");

        tzDisplayMap.put("Pacific/Guadalcanal", "Solomon Is.");

        tzDisplayMap.put("Pacific/Guam", "Guam");

        tzDisplayMap.put("Pacific/Honolulu", "Hawaii");

        tzDisplayMap.put("Pacific/Majuro", "Marshall Is.");

        tzDisplayMap.put("Pacific/Midway", "Midway Island");

        tzDisplayMap.put("Pacific/Noumea", "New Caledonia");

        tzDisplayMap.put("Pacific/Pago_Pago", "American Samoa");

        tzDisplayMap.put("Pacific/Port_Moresby", "Port Moresby");

        tzDisplayMap.put("Pacific/Tongatapu", "Nuku'alofa");
    }

    static {

        tzCanonicalMap.put("International Date Line West", "Pacific/Midway");

        tzCanonicalMap.put("Midway Island", "Pacific/Midway");

        tzCanonicalMap.put("American Samoa", "Pacific/Pago_Pago");

        tzCanonicalMap.put("Hawaii", "Pacific/Honolulu");

        tzCanonicalMap.put("Alaska", "America/Juneau");

        tzCanonicalMap.put("Pacific Time (US & Canada)", "America/Los_Angeles");

        tzCanonicalMap.put("Tijuana", "America/Tijuana");

        tzCanonicalMap.put("Mountain Time (US & Canada)", "America/Denver");

        tzCanonicalMap.put("Arizona", "America/Phoenix");

        tzCanonicalMap.put("Chihuahua", "America/Chihuahua");

        tzCanonicalMap.put("Mazatlan", "America/Mazatlan");

        tzCanonicalMap.put("Central Time (US & Canada)", "America/Chicago");

        tzCanonicalMap.put("Saskatchewan", "America/Regina");

        tzCanonicalMap.put("Guadalajara", "America/Mexico_City");

        tzCanonicalMap.put("Mexico City", "America/Mexico_City");

        tzCanonicalMap.put("Monterrey", "America/Monterrey");

        tzCanonicalMap.put("Central America", "America/Guatemala");

        tzCanonicalMap.put("Eastern Time (US & Canada)", "America/New_York");

        tzCanonicalMap.put("Indiana (East)", "America/Indiana/Indianapolis");

        tzCanonicalMap.put("Bogota", "America/Bogota");

        tzCanonicalMap.put("Lima", "America/Lima");

        tzCanonicalMap.put("Quito", "America/Lima");

        tzCanonicalMap.put("Atlantic Time (Canada)", "America/Halifax");

        tzCanonicalMap.put("Caracas", "America/Caracas");

        tzCanonicalMap.put("La Paz", "America/La_Paz");

        tzCanonicalMap.put("Santiago", "America/Santiago");

        tzCanonicalMap.put("Newfoundland", "America/St_Johns");

        tzCanonicalMap.put("Brasilia", "America/Sao_Paulo");

        tzCanonicalMap.put("Buenos Aires", "America/Argentina/Buenos_Aires");

        tzCanonicalMap.put("Montevideo", "America/Montevideo");

        tzCanonicalMap.put("Georgetown", "America/Guyana");

        tzCanonicalMap.put("Greenland", "America/Godthab");

        tzCanonicalMap.put("Mid-Atlantic", "Atlantic/South_Georgia");

        tzCanonicalMap.put("Azores", "Atlantic/Azores");

        tzCanonicalMap.put("Cape Verde Is.", "Atlantic/Cape_Verde");

        tzCanonicalMap.put("Dublin", "Europe/Dublin");

        tzCanonicalMap.put("Edinburgh", "Europe/London");

        tzCanonicalMap.put("Lisbon", "Europe/Lisbon");

        tzCanonicalMap.put("London", "Europe/London");

        tzCanonicalMap.put("Casablanca", "Africa/Casablanca");

        tzCanonicalMap.put("Monrovia", "Africa/Monrovia");

        tzCanonicalMap.put("UTC", "Etc/UTC");

        tzCanonicalMap.put("Belgrade", "Europe/Belgrade");

        tzCanonicalMap.put("Bratislava", "Europe/Bratislava");

        tzCanonicalMap.put("Budapest", "Europe/Budapest");

        tzCanonicalMap.put("Ljubljana", "Europe/Ljubljana");

        tzCanonicalMap.put("Prague", "Europe/Prague");

        tzCanonicalMap.put("Sarajevo", "Europe/Sarajevo");

        tzCanonicalMap.put("Skopje", "Europe/Skopje");

        tzCanonicalMap.put("Warsaw", "Europe/Warsaw");

        tzCanonicalMap.put("Zagreb", "Europe/Zagreb");

        tzCanonicalMap.put("Brussels", "Europe/Brussels");

        tzCanonicalMap.put("Copenhagen", "Europe/Copenhagen");

        tzCanonicalMap.put("Madrid", "Europe/Madrid");

        tzCanonicalMap.put("Paris", "Europe/Paris");

        tzCanonicalMap.put("Amsterdam", "Europe/Amsterdam");

        tzCanonicalMap.put("Berlin", "Europe/Berlin");

        tzCanonicalMap.put("Bern", "Europe/Berlin");

        tzCanonicalMap.put("Rome", "Europe/Rome");

        tzCanonicalMap.put("Stockholm", "Europe/Stockholm");

        tzCanonicalMap.put("Vienna", "Europe/Vienna");

        tzCanonicalMap.put("West Central Africa", "Africa/Algiers");

        tzCanonicalMap.put("Bucharest", "Europe/Bucharest");

        tzCanonicalMap.put("Cairo", "Africa/Cairo");

        tzCanonicalMap.put("Helsinki", "Europe/Helsinki");

        tzCanonicalMap.put("Kyiv", "Europe/Kiev");

        tzCanonicalMap.put("Riga", "Europe/Riga");

        tzCanonicalMap.put("Sofia", "Europe/Sofia");

        tzCanonicalMap.put("Tallinn", "Europe/Tallinn");

        tzCanonicalMap.put("Vilnius", "Europe/Vilnius");

        tzCanonicalMap.put("Athens", "Europe/Athens");

        tzCanonicalMap.put("Istanbul", "Europe/Istanbul");

        tzCanonicalMap.put("Minsk", "Europe/Minsk");

        tzCanonicalMap.put("Jerusalem", "Asia/Jerusalem");

        tzCanonicalMap.put("Harare", "Africa/Harare");

        tzCanonicalMap.put("Pretoria", "Africa/Johannesburg");

        tzCanonicalMap.put("Moscow", "Europe/Moscow");

        tzCanonicalMap.put("St. Petersburg", "Europe/Moscow");

        tzCanonicalMap.put("Volgograd", "Europe/Moscow");

        tzCanonicalMap.put("Kuwait", "Asia/Kuwait");

        tzCanonicalMap.put("Riyadh", "Asia/Riyadh");

        tzCanonicalMap.put("Nairobi", "Africa/Nairobi");

        tzCanonicalMap.put("Baghdad", "Asia/Baghdad");

        tzCanonicalMap.put("Tehran", "Asia/Tehran");

        tzCanonicalMap.put("Abu Dhabi", "Asia/Muscat");

        tzCanonicalMap.put("Muscat", "Asia/Muscat");

        tzCanonicalMap.put("Baku", "Asia/Baku");

        tzCanonicalMap.put("Tbilisi", "Asia/Tbilisi");

        tzCanonicalMap.put("Yerevan", "Asia/Yerevan");

        tzCanonicalMap.put("Kabul", "Asia/Kabul");

        tzCanonicalMap.put("Ekaterinburg", "Asia/Yekaterinburg");

        tzCanonicalMap.put("Islamabad", "Asia/Karachi");

        tzCanonicalMap.put("Karachi", "Asia/Karachi");

        tzCanonicalMap.put("Tashkent", "Asia/Tashkent");

        tzCanonicalMap.put("Chennai", "Asia/Kolkata");

        tzCanonicalMap.put("Kolkata", "Asia/Kolkata");

        tzCanonicalMap.put("Mumbai", "Asia/Kolkata");

        tzCanonicalMap.put("New Delhi", "Asia/Kolkata");

        tzCanonicalMap.put("Kathmandu", "Asia/Kathmandu");

        tzCanonicalMap.put("Astana", "Asia/Dhaka");

        tzCanonicalMap.put("Dhaka", "Asia/Dhaka");

        tzCanonicalMap.put("Sri Jayawardenepura", "Asia/Colombo");

        tzCanonicalMap.put("Almaty", "Asia/Almaty");

        tzCanonicalMap.put("Novosibirsk", "Asia/Novosibirsk");

        tzCanonicalMap.put("Rangoon", "Asia/Rangoon");

        tzCanonicalMap.put("Bangkok", "Asia/Bangkok");

        tzCanonicalMap.put("Hanoi", "Asia/Bangkok");

        tzCanonicalMap.put("Jakarta", "Asia/Jakarta");

        tzCanonicalMap.put("Krasnoyarsk", "Asia/Krasnoyarsk");

        tzCanonicalMap.put("Beijing", "Asia/Shanghai");

        tzCanonicalMap.put("Chongqing", "Asia/Chongqing");

        tzCanonicalMap.put("Hong Kong", "Asia/Hong_Kong");

        tzCanonicalMap.put("Urumqi", "Asia/Urumqi");

        tzCanonicalMap.put("Kuala Lumpur", "Asia/Kuala_Lumpur");

        tzCanonicalMap.put("Singapore", "Asia/Singapore");

        tzCanonicalMap.put("Taipei", "Asia/Taipei");

        tzCanonicalMap.put("Perth", "Australia/Perth");

        tzCanonicalMap.put("Irkutsk", "Asia/Irkutsk");

        tzCanonicalMap.put("Ulaanbaatar", "Asia/Ulaanbaatar");

        tzCanonicalMap.put("Seoul", "Asia/Seoul");

        tzCanonicalMap.put("Osaka", "Asia/Tokyo");

        tzCanonicalMap.put("Sapporo", "Asia/Tokyo");

        tzCanonicalMap.put("Tokyo", "Asia/Tokyo");

        tzCanonicalMap.put("Yakutsk", "Asia/Yakutsk");

        tzCanonicalMap.put("Darwin", "Australia/Darwin");

        tzCanonicalMap.put("Adelaide", "Australia/Adelaide");

        tzCanonicalMap.put("Canberra", "Australia/Melbourne");

        tzCanonicalMap.put("Melbourne", "Australia/Melbourne");

        tzCanonicalMap.put("Sydney", "Australia/Sydney");

        tzCanonicalMap.put("Brisbane", "Australia/Brisbane");

        tzCanonicalMap.put("Hobart", "Australia/Hobart");

        tzCanonicalMap.put("Vladivostok", "Asia/Vladivostok");

        tzCanonicalMap.put("Guam", "Pacific/Guam");

        tzCanonicalMap.put("Port Moresby", "Pacific/Port_Moresby");

        tzCanonicalMap.put("Magadan", "Asia/Magadan");

        tzCanonicalMap.put("Solomon Is.", "Pacific/Guadalcanal");

        tzCanonicalMap.put("New Caledonia", "Pacific/Noumea");

        tzCanonicalMap.put("Fiji", "Pacific/Fiji");

        tzCanonicalMap.put("Kamchatka", "Asia/Kamchatka");

        tzCanonicalMap.put("Marshall Is.", "Pacific/Majuro");

        tzCanonicalMap.put("Auckland", "Pacific/Auckland");

        tzCanonicalMap.put("Wellington", "Pacific/Auckland");

        tzCanonicalMap.put("Nuku'alofa", "Pacific/Tongatapu");

        tzCanonicalMap.put("Tokelau Is.", "Pacific/Fakaofo");

        tzCanonicalMap.put("Chatham Is.", "Pacific/Chatham");

        tzCanonicalMap.put("Samoa", "Pacific/Apia");
    }

    static {

        tzNoLongerSupported.put("ACT", Boolean.valueOf(true));

        tzNoLongerSupported.put("AET", Boolean.valueOf(true));

        tzNoLongerSupported.put("AGT", Boolean.valueOf(true));

        tzNoLongerSupported.put("ART", Boolean.valueOf(true));

        tzNoLongerSupported.put("AST", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Abidjan", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Accra", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Addis_Ababa", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Asmara", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Banjul", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Conakry", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Dakar", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Dar_es_Salaam", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Douala", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Khartoum", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Lagos", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Sao_Tome", Boolean.valueOf(true));

        tzNoLongerSupported.put("Africa/Windhoek", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Adak", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Anchorage", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Argentina/Cordoba", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Argentina/Mendoza", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Asuncion", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Barbados", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Boise", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Buenos_Aires", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Cancun", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Costa_Rica", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Curacao", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Detroit", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Edmonton", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Fortaleza", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Guayaquil", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Indianapolis", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Kentucky/Louisville", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Knox_IN", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Matamoros", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Montreal", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Recife", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Santo_Domingo", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Toronto", Boolean.valueOf(true));

        tzNoLongerSupported.put("America/Vancouver", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Amman", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Bahrain", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Beirut", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Bishkek", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Calcutta", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Chungking", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Dili", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Dubai", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Harbin", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Ho_Chi_Minh", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Istanbul", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Jayapura", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Katmandu", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Macao", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Macau", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Makassar", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Manila", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Omsk", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Phnom_Penh", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Saigon", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Sakhalin", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Samarkand", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Tel_Aviv", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Ujung_Pandang", Boolean.valueOf(true));

        tzNoLongerSupported.put("Asia/Ulan_Bator", Boolean.valueOf(true));

        tzNoLongerSupported.put("Atlantic/Reykjavik", Boolean.valueOf(true));

        tzNoLongerSupported.put("Australia/ACT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Australia/NSW", Boolean.valueOf(true));

        tzNoLongerSupported.put("Australia/Queensland", Boolean.valueOf(true));

        tzNoLongerSupported.put("Australia/Victoria", Boolean.valueOf(true));

        tzNoLongerSupported.put("BET", Boolean.valueOf(true));

        tzNoLongerSupported.put("BST", Boolean.valueOf(true));

        tzNoLongerSupported.put("Brazil/East", Boolean.valueOf(true));

        tzNoLongerSupported.put("Brazil/West", Boolean.valueOf(true));

        tzNoLongerSupported.put("CAT", Boolean.valueOf(true));

        tzNoLongerSupported.put("CET", Boolean.valueOf(true));

        tzNoLongerSupported.put("CST", Boolean.valueOf(true));

        tzNoLongerSupported.put("CST6CDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Atlantic", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Central", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Eastern", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Mountain", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Pacific", Boolean.valueOf(true));

        tzNoLongerSupported.put("Canada/Saskatchewan", Boolean.valueOf(true));

        tzNoLongerSupported.put("Chile/Continental", Boolean.valueOf(true));

        tzNoLongerSupported.put("EAT", Boolean.valueOf(true));

        tzNoLongerSupported.put("EET", Boolean.valueOf(true));

        tzNoLongerSupported.put("EST", Boolean.valueOf(true));

        tzNoLongerSupported.put("EST5EDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Egypt", Boolean.valueOf(true));

        tzNoLongerSupported.put("Eire", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+0", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+1", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+11", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+2", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+3", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+4", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+5", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+6", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+7", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+8", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT+9", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-0", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-2", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-4", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-5", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-6", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-8", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT-9", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/GMT0", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/Greenwich", Boolean.valueOf(true));

        tzNoLongerSupported.put("Etc/UCT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Andorra", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Chisinau", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Kaliningrad", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Luxembourg", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Malta", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Oslo", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Simferopol", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Vatican", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Volgograd", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Zaporozhye", Boolean.valueOf(true));

        tzNoLongerSupported.put("Europe/Zurich", Boolean.valueOf(true));

        tzNoLongerSupported.put("GB", Boolean.valueOf(true));

        tzNoLongerSupported.put("GB-Eire", Boolean.valueOf(true));

        tzNoLongerSupported.put("GMT", Boolean.valueOf(true));

        tzNoLongerSupported.put("GMT0", Boolean.valueOf(true));

        tzNoLongerSupported.put("Greenwich", Boolean.valueOf(true));

        tzNoLongerSupported.put("Hongkong", Boolean.valueOf(true));

        tzNoLongerSupported.put("IET", Boolean.valueOf(true));

        tzNoLongerSupported.put("IST", Boolean.valueOf(true));

        tzNoLongerSupported.put("Iceland", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Antananarivo", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Chagos", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Christmas", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Cocos", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Comoro", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Kerguelen", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Mahe", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Maldives", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Mauritius", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Mayotte", Boolean.valueOf(true));

        tzNoLongerSupported.put("Indian/Reunion", Boolean.valueOf(true));

        tzNoLongerSupported.put("Iran", Boolean.valueOf(true));

        tzNoLongerSupported.put("Israel", Boolean.valueOf(true));

        tzNoLongerSupported.put("JST", Boolean.valueOf(true));

        tzNoLongerSupported.put("Japan", Boolean.valueOf(true));

        tzNoLongerSupported.put("Kwajalein", Boolean.valueOf(true));

        tzNoLongerSupported.put("MET", Boolean.valueOf(true));

        tzNoLongerSupported.put("MST", Boolean.valueOf(true));

        tzNoLongerSupported.put("MST7MDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Mexico/General", Boolean.valueOf(true));

        tzNoLongerSupported.put("NET", Boolean.valueOf(true));

        tzNoLongerSupported.put("NZ", Boolean.valueOf(true));

        tzNoLongerSupported.put("PRC", Boolean.valueOf(true));

        tzNoLongerSupported.put("PST", Boolean.valueOf(true));

        tzNoLongerSupported.put("PST8PDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Pacific/Chuuk", Boolean.valueOf(true));

        tzNoLongerSupported.put("Poland", Boolean.valueOf(true));

        tzNoLongerSupported.put("Portugal", Boolean.valueOf(true));

        tzNoLongerSupported.put("ROK", Boolean.valueOf(true));

        tzNoLongerSupported.put("SST", Boolean.valueOf(true));

        tzNoLongerSupported.put("Singapore", Boolean.valueOf(true));

        tzNoLongerSupported.put("SystemV/EST5", Boolean.valueOf(true));

        tzNoLongerSupported.put("SystemV/MST7MDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("SystemV/PST8PDT", Boolean.valueOf(true));

        tzNoLongerSupported.put("Turkey", Boolean.valueOf(true));

        tzNoLongerSupported.put("UCT", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Alaska", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Aleutian", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Arizona", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Central", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/East-Indiana", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Eastern", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Indiana-Starke", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Mountain", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Pacific", Boolean.valueOf(true));

        tzNoLongerSupported.put("US/Pacific-New", Boolean.valueOf(true));

        tzNoLongerSupported.put("UTC,225", Boolean.valueOf(true));

        tzNoLongerSupported.put("Universal", Boolean.valueOf(true));
    }

    public static String getTimeZoneCanonicalName(String tzDisplayName) {

        return tzCanonicalMap.get(tzDisplayName);
    }


    public static String getTimeZoneDisplayName(String tzCanonicalName) {

        String displayName = tzDisplayMap.get(tzCanonicalName);


        return (displayName != null) ? displayName : tzCanonicalName;
    }

    public static String getTimeZoneDisplayShort(String tzCanonicalName) {

        TimeZone tz = TimeZone.getTimeZone(tzCanonicalName);

        if (tz == null) {

            return tzCanonicalName;
        }

        return tz.getDisplayName(tz.inDaylightTime(new Date()), 0);
    }

    public static String[] getTimeZoneDisplayNames() {

        Object[] tzKeys = tzCanonicalMap.keySet().toArray();

        String[] result = new String[tzKeys.length];

        for (int k = 0; k < tzKeys.length; k++) {

            result[k] = (String) tzKeys[k];
        }

        return result;
    }

    public static boolean isValidTimeZone(String tzCanonicalName) {

        return tzDisplayMap.containsKey(tzCanonicalName);
    }

    public static boolean isTimeZoneNoLongerSupported(String tzCanonicalName) {

        return tzNoLongerSupported.containsKey(tzCanonicalName);
    }

    public static double getOffset(String tzCanonicalName) {

        TimeZone timeZone = TimeZone.getTimeZone(tzCanonicalName);

        double offset = timeZone.getOffset(System.currentTimeMillis()) / 3600000.0D;

        return offset;
    }

    public static String getDisplayWithOffset(String pDisplayName) {

        String tzCanonicalName = getTimeZoneCanonicalName(pDisplayName);

        double offset = getOffset(tzCanonicalName);


        String sign = (offset > 0.0D) ? "+" : "-";

        double absoluteOffset = Math.abs(offset);

        Double hours = Double.valueOf(Math.floor(absoluteOffset));

        Double minutes = Double.valueOf((absoluteOffset - hours.doubleValue()) * 60.0D);


        String hoursString = Integer.toString(hours.intValue());

        String minutesString = Integer.toString(minutes.intValue());


        if (hours.doubleValue() < 10.0D) {

            hoursString = "0" + hoursString;
        }

        if (minutes.doubleValue() < 10.0D) {

            minutesString = "0" + minutesString;
        }


        return "(" + sign + hoursString + ":" + minutesString + ") " + pDisplayName;
    }
}
