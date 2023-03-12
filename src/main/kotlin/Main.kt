import java.io.FileWriter
import java.time.Instant
import java.time.ZoneOffset

fun main(args: Array<String>) {
    val objectValue = 300
    //val ARRIVAL_TIMES = listOf(1678442763, 1678442889, 1678443054, 1678444648, 1678444873, 1678445969, 1678446829, 1678447507, 1678451401, 1678451760, 1678453237, 1678453590, 1678453678, 1678455291, 1678457685, 1678457708, 1678459289, 1678461696, 1678462408, 1678463677)
    //val DEPART_TIMES = listOf(1678454064, 1678452469, 1678453960, 1678456239, 1678447387, 1678448991, 1678455940, 1678451428, 1678458877, 1678458328, 1678466840, 1678461061, 1678465815, 1678459199, 1678471058, 1678460652, 1678465384, 1678473645, 1678465911, 1678474316)
    //val ARRIVAL_TIMES = listOf(1678439163, 1678442889, 1678443054, 1678444648, 1678444873, 1678445969, 1678446829, 1678447507, 1678451401, 1678455360)
    //val DEPART_TIMES = listOf(1678446864, 1678452469, 1678446760, 1678456239, 1678447387, 1678448991, 1678455940, 1678451428, 1678458877, 1678461928)

    val ARRIVAL_TIMES = listOf(1678442763, 1678442889, 1678443054, 1678444648, 1678444873, 1678445969, 1678446829, 1678447507, 1678451401, 1678451760, 1678453237, 1678453590, 1678453678, 1678455291, 1678457685)
    val DEPART_TIMES = listOf(1678454064, 1678452469, 1678453960, 1678456239, 1678447387, 1678448991, 1678455940, 1678451428, 1678458877, 1678458328, 1678466840, 1678461061, 1678465815, 1678459199, 1678471058)

    val GATE_DISTANCE = listOf(
        listOf(0, 300, 600, 900, 1200, 1500, 1800, 2100),
        listOf(300, 0, 300, 600, 900, 1200, 1500, 1800),
        listOf(600, 300, 0, 300, 600, 900, 1200, 1500),
        listOf(900, 600, 300, 0, 300, 600, 900, 1200),
        listOf(1200, 900, 600, 300, 0, 300, 600, 900),
        listOf(1500, 1200, 900, 600, 300, 0, 300, 600),
        listOf(1800, 1500, 1200, 900, 600, 300, 0, 300),
        listOf(2100, 1800, 1500, 1200, 900, 600, 300, 0)
    )
    val GATE_ASSIGNMENTS = listOf(1,2,3,4,5,6,7,8,9,10,1,2,3,4,5)
    val gatesInt = 12

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
    val foreString = "<html lang=\"\">\n<body>\n<svg height=\"${flights.size*83.15}\" width=\"1850\">\n"
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
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((index+1)*80)-17.5}\" width=\"${(191.12+(1656.38*dep_percent))-(191.12+(1656.38*arr_percent))}\" height=\"15\" style=\"fill:rgb(0,0,0);\"/>\n"
        // Circles - starting 191.12, ending 1847.5
        //stringContent += "  <circle cx=\"${191.12+(1656.38*arr_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"10\" fill=\"green\"/>\n"
        //stringContent += "  <circle cx=\"${191.12+(1656.38*dep_percent)}\" cy=\"${((index+1)*80)-10}\" r=\"10\" fill=\"red\"/>\n"
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((index+1)*80)-17.5}\" width=\"3\" height=\"15\" fill=\"green\"/>\n"
        stringContent += "  <rect x=\"${191.12+(1656.38*dep_percent)}\" y=\"${((index+1)*80)-17.5}\" width=\"3\" height=\"15\" fill=\"red\"/>\n"

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
        stringContent += "  <text x=\"75\" y=\"${(i+1)*80}\" font-family=\"monospace\" font-size=\"2em\">Gate ${i+1}</text>\n"
        // Divider
        stringContent += "  <line x1=\"0\" y1=\"${((i+1)*80)+30}\" x2=\"1845.5\" y2=\"${((i+1)*80)+30}\" style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\n"
    }


    flights.sortedByDescending { it.flightArr}.forEachIndexed { _, flight ->
        val arr_time = Instant.ofEpochSecond(flight.flightArr.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val arr_percent: Float = (arr_time.hour.toFloat() / 24) + (arr_time.minute.toFloat() / (60 * 24))
        val dep_time = Instant.ofEpochSecond(flight.flightDep.toLong()).atOffset(ZoneOffset.UTC).toLocalTime()
        val dep_percent: Float = (dep_time.hour.toFloat() / 24) + (dep_time.minute.toFloat() / (60 * 24))

        // Black Rect
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((flight.gateAssignment) * 80) - 17.5}\" width=\"${(191.12+(1656.38*dep_percent))-(191.12+(1656.38*arr_percent))}\" height=\"15\" style=\"fill:rgb(0,0,0);\"/>\n"

        // Rects - starting 191.12, ending 1847.5
        stringContent += "  <rect x=\"${191.12+(1656.38*arr_percent)}\" y=\"${((flight.gateAssignment) * 80) - 17.5}\" width=\"3\" height=\"15\" fill=\"green\"/>\n"
        stringContent += "  <rect x=\"${191.12+(1656.38*dep_percent)}\" y=\"${((flight.gateAssignment) * 80) - 17.5}\" width=\"3\" height=\"15\" fill=\"red\"/>\n"

        stringContent += "  <text fill=\"white\" x=\"${((191.12+(1656.38*arr_percent))+(((dep_percent-arr_percent))/2)+10)}\" y=\"${((flight.gateAssignment)*80)-6}\" font-family=\"monospace\" font-size=\"1em\">F#${flight.flightID}</text>\n"

    }

    val htmlText = foreString + stringContent + endString
    FileWriter("src/main/res/svgOutput/gateSched.html").use { it.write(htmlText) }
}