
function initContextMenu() {
    jQuery(".context-selector").click(function () {
        jQuery(".context-menu").toggleClass("--context-expand");
    });
    jQuery('#user-header input').on('input', function () {
        var keyword = jQuery(this).val().toLowerCase();
        console.log("searching for: " + keyword);
        jQuery(".context-menu li").each(function () {
            let context = jQuery(this).find("a").text().toLowerCase();
            if (context.indexOf(keyword) > -1) {
                jQuery(this).show();
            } else {
                jQuery(this).hide();
            }
        });
    });
}

jQuery(document).ready(function () {
    initContextMenu();
});

//DATA

PrimeFaces.locales['de'] = {
    closeText: 'Schließen',
    prevText: 'Zurück',
    nextText: 'Weiter',
    monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli',
        'August', 'September', 'Oktober', 'November', 'Dezember'],
    monthNamesShort: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul',
        'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
    dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag',
        'Freitag', 'Samstag'],
    dayNamesShort: ['Son', 'Mon', 'Die', 'Mit', 'Don', 'Fre', 'Sam'],
    dayNamesMin: ['So', 'Mo', 'Di', 'Mi ', 'Do', 'Fr ', 'Sa'],
    weekHeader: 'KW',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Nur Zeit',
    timeText: 'Zeit',
    hourText: 'Stunde',
    minuteText: 'Minute',
    secondText: 'Sekunde',
    currentText: 'Aktuelles Datum',
    ampm: false,
    month: 'Monat',
    week: 'Woche',
    day: 'Tag',
    allDayText: 'Ganzer Tag'
};
