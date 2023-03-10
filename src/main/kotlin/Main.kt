import java.io.FileWriter
import java.time.Instant
import java.time.ZoneOffset

fun main(args: Array<String>) {
    val objectValue = 300
    //val ARRIVAL_TIMES = listOf(1678096800, 1678097040, 1678097100, 1678098900, 1678100160, 1678100160, 1678100940, 1678102260, 1678103940, 1678105200)
    val ARRIVAL_TIMES = listOf(1646970000, 1646973600, 1646977200, 1646980800, 1646984400, 1646988000, 1646991600, 1646995200, 1646998800, 1647002400, 1647006000, 1647009600, 1647013200, 1647016800, 1647020400, 1647024000, 1647027600, 1647031200, 1647034800, 1647038400)
    //val DEPART_TIMES = listOf(1678100340, 1678104240, 1678099500, 1678104240, 1678105800, 1678102440, 1678107180, 1678107840, 1678107540, 1678109040)
    val DEPART_TIMES = listOf(1646975400, 1646979000, 1646982600, 1646986200, 1646989800, 1646993400, 1646997000, 1647000600, 1647004200, 1647007800, 1647011400, 1647015000, 1647018600, 1647022200, 1647025800, 1647029400, 1647033000, 1647036600, 1647040200, 1647040200)
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
    val GATE_ASSIGNMENTS = listOf(6,6,7,6,7,6,7,6,7,6,7,6,7,6,7,6,7,6,7,6)
    val gatesInt = 8

    val flights = mutableListOf<Flight>()

    for (i in ARRIVAL_TIMES.indices) {
        flights += Flight(i+1, ARRIVAL_TIMES[i], DEPART_TIMES[i], GATE_ASSIGNMENTS[i])
    }

    println("Generating schedule SVG for 24 hour period with ${flights.size} flights...")
    println("Generating assignment SVG for ${flights.size} flights, with $gatesInt gates...")
    generateScheduleFile(flights)
    generateGateFile(flights, gatesInt)
}

private fun generateScheduleFile(flights: List<Flight>) {
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
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((index+1)*80)-12.5}\" width=\"${(191.12+(1656.38*dep_percent))-(191.12+(1656.38*arr_percent))}\" height=\"5\" style=\"fill:rgb(0,0,0);\"/>\n"
        // Circles - starting 191.12, ending 1847.5
        stringContent += "  <circle cx=\"${191.12+(1656.38*arr_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"7.5\" fill=\"green\"/>\n"
        stringContent += "  <circle cx=\"${191.12+(1656.38*dep_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"7.5\" fill=\"red\"/>\n"

        // Divider
        stringContent += "  <line x1=\"0\" y1=\"${((index+1)*80)+30}\" x2=\"1845.5\" y2=\"${((index+1)*80)+30}\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    }

    val htmlText = foreString + stringContent + endString
    FileWriter("src/main/res/svgOutput/timeSched.html").use { it.write(htmlText) }
}

private fun generateGateFile(flights: List<Flight>, gatesInt: Int) {
    val foreString = "<html lang=\"\">\n<body>\n<svg height=\"${gatesInt*83.75}\" width=\"1850\">\n"
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
    for(i in 0..gatesInt) {
        stringContent += "  <text x=\"62.5\" y=\"${(i+1)*80}\" font-family=\"monospace\" font-size=\"2em\">Gate ${i+1}</text>\n"
        // Divider
        stringContent += "  <line x1=\"0\" y1=\"${((i+1)*80)+30}\" x2=\"1845.5\" y2=\"${((i+1)*80)+30}\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    }


    flights.sortedByDescending { it.flightArr}.forEachIndexed { _, flight ->
        val arr_time = Instant.ofEpochSecond(flight.flightArr.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val arr_percent: Float = (arr_time.hour.toFloat() / 24) + (arr_time.minute.toFloat() / (60 * 24))
        val dep_time = Instant.ofEpochSecond(flight.flightDep.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val dep_percent: Float = (dep_time.hour.toFloat() / 24) + (dep_time.minute.toFloat() / (60 * 24))

        // Black Rect
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((flight.gateAssignment) * 80) - 12.5}\" width=\"${(191.12+(1656.38*dep_percent))-(191.12+(1656.38*arr_percent))}\" height=\"5\" style=\"fill:rgb(0,0,0);\"/>\n"
        // Circles - starting 191.12, ending 1847.5
        stringContent += "  <circle cx=\"${191.12+(1656.38*arr_percent)}\" cy=\"${((flight.gateAssignment)*80)-10}\" r=\"7.5\" fill=\"green\"/>\n"
        stringContent += "  <circle cx=\"${191.12+(1656.38*dep_percent)}\" cy=\"${((flight.gateAssignment)*80)-10}\" r=\"7.5\" fill=\"red\"/>\n"

    }

    val htmlText = foreString + stringContent + endString
    FileWriter("src/main/res/svgOutput/gateSched.html").use { it.write(htmlText) }
}