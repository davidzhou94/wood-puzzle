package woodpuzzle.engine

import org.xml.sax.SAXException
import woodpuzzle.engine.XMLReader.buildPuzzle
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.HaltingDFSSolver
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException

object Engine {
    private lateinit var puzzle: Puzzle

    /**
     * Run using:
     * $ java -Xmx8g -cp F:\repos\wood-puzzle\WoodPuzzle\bin\ woodPuzzle.engine.Engine .\assets\default.xml
     * @param args first argument is the path to the puzzle definition
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            puzzle = buildPuzzle(args[0])
        } catch (ex: SAXException) {
            println("Error reading XML file: " + ex.message)
            return
        } catch (ex: ParserConfigurationException) {
            println("Error reading XML file: " + ex.message)
            return
        } catch (ex: IOException) {
            println("Error locating or opening given file: " + ex.message)
            return
        }

        HaltingDFSSolver(puzzle).solvePuzzle()
    }
}