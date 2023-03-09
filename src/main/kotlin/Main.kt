import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant
import java.time.ZoneOffset

fun main(args: Array<String>) {
    val objectValue = 300
    val ARRIVAL_TIMES = listOf(1678096800, 1678097040, 1678097100, 1678098900, 1678100160, 1678100160, 1678100940, 1678102260, 1678103940, 1678105200, 1678105200, 1678105200)
    val DEPART_TIMES = listOf(1678100340, 1678104240, 1678099500, 1678104240, 1678105800, 1678102440, 1678107180, 1678107840, 1678107540, 1678109040, 1678109040, 1678109040)
    val GATE_DISTANCE = listOf(
        listOf(0, 300, 600, 900, 1200, 1500, 1800, 2100),
        listOf(0, 300, 600, 900, 1200, 1500, 1800, 2100),
        listOf(300, 0, 300, 600, 900, 1200, 1500, 1800),
        listOf(600, 300, 0, 300, 600, 900, 1200, 1500),
        listOf(900, 600, 300, 0, 300, 600, 900, 1200),
        listOf(1200, 900, 600, 300, 0, 300, 600, 900),
        listOf(1500, 1200, 900, 600, 300, 0, 300, 600),
        listOf(1800, 1500, 1200, 900, 600, 300, 0, 300),
        listOf(2100, 1800, 1500, 1200, 900, 600, 300, 0)
    )
    val GATE_ASSIGNMENTS = listOf(1, 3, 2, 3, 4, 2, 4, 1, 2, 3, 2, 4)

    val flights = mutableListOf<Flight>()

    for (i in ARRIVAL_TIMES.indices) {
        flights += Flight(i+1, ARRIVAL_TIMES[i], DEPART_TIMES[i], GATE_ASSIGNMENTS[i])
    }
    generateHTMLFile(flights)
}




private fun generateHTMLFile(flights: List<Flight>) {
    val foreString = "<html lang=\"\">\n<body>\n<svg height=\"${flights.size*82.5}\" width=\"1850\">\n"
    val endString = "</svg>\n</body>\n</html>\n"

    var stringContent = ""

    for(i in 0 until 23) {
        stringContent += "  <text x=\"${282.5+((1675/24)*i)}\" y=\"20\" font-family=\"monospace\" font-size=\"1.5em\">${"%02d".format(i+1)}</text>\n"
        stringContent += "  <line x1=\"${260+((1675/24)*i)}\" y1=\"0\" x2=\"${260+((1675/24)*i)}\" y2=\"1800\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
        for(x in 0 until 4) {
            if(i == 0 && x < 2) continue
            stringContent += "  <line x1=\"${225 + ((1675 / 24) * i) + (x * 17.5)}\" y1=\"30\" x2=\"${225 + ((1675 / 24) * i) + (x * 17.5)}\" y2=\"1800\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
        }
    }
    stringContent += "  <line x1=\"${260+((1675/24)*23)}\" y1=\"0\" x2=\"${260+((1675/24)*23)}\" y2=\"1800\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    for(x in 0 until 4) {
        stringContent += "  <line x1=\"${225 + ((1675 / 24) * 23) + (x * 17.5)}\" y1=\"30\" x2=\"${225 + ((1675 / 24) * 23) + (x * 17.5)}\" y2=\"1800\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    }

    stringContent += "  <line x1=\"0\" y1=\"30\" x2=\"1845.5\" y2=\"30\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    flights.forEachIndexed { index, flight ->

        val arr_time = Instant.ofEpochSecond(flight.flightArr.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val arr_percent: Float = (arr_time.hour.toFloat() / 24) + (arr_time.minute.toFloat() / (60 * 24))
        val dep_time = Instant.ofEpochSecond(flight.flightDep.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val dep_percent: Float = (dep_time.hour.toFloat() / 24) + (dep_time.minute.toFloat() / (60 * 24))

        // Text
        stringContent += "  <text x=\"62.5\" y=\"${(index+1)*80}\" font-family=\"monospace\" font-size=\"2em\">Flight ${flight.flightID}</text>\n"
        // Black Rect
        stringContent += "  <rect x=\"${225+(1575*arr_percent)}\" y=\"${((index+1)*80)-15}\" width=\"${(225+(1575*dep_percent))-(225+(1575*arr_percent))}\" height=\"10\" style=\"fill:rgb(0,0,0);\"/>\n"
        // Circles - starting 225, ending 1800
        stringContent += "  <circle cx=\"${225+(1575*arr_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"10\" fill=\"green\"/>\n"
        stringContent += "  <circle cx=\"${225+(1575*dep_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"10\" fill=\"red\"/>\n"

        // Divider
        stringContent += "  <line x1=\"0\" y1=\"${((index+1)*80)+30}\" x2=\"1845.5\" y2=\"${((index+1)*80)+30}\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    }

    val htmlText = foreString + stringContent + endString
    FileWriter("src/main/res/svgOutput/index.html").use { it.write(htmlText) }
}