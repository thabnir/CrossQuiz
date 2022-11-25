import com.opencsv.CSVReader
import java.io.FileNotFoundException
import java.time.format.DateTimeFormatter
import java.util.*

class Quizzer(vararg dataSources: DataSource = DataSource.values()) {
    val questions = LinkedList(DataSource.loadQuestions(*dataSources))
    var currentQuestionIndex = 0
    var correctAnswers = 0
    var incorrectAnswers = 0
    val currentQuestion: Question
        get() = questions[currentQuestionIndex]
    val questionsAnswered: Int
        get() = correctAnswers + incorrectAnswers
    val score: Int
        get() = (correctAnswers - incorrectAnswers) * 100
    val percentRight: Float
        get() = (correctAnswers.toFloat() / questionsAnswered.toFloat() * 100)

    fun answerQuestion(input: String): Boolean {
        currentQuestion.guess(input)
        val correct = currentQuestion.answeredCorrectly!!
        if (correct) correctAnswers++ else incorrectAnswers++
        currentQuestion.guesses++
        currentQuestionIndex++
        return correct
    }

    fun nextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
    }
}

enum class DataSource(val fileName: String) {
    NYT_CROSSWORDS("nytcrosswords.csv") {
        override fun parseQuestion(line: Array<String>): Question {
            return CrosswordQuestion(parseDate(line[0]), line[2], line[1])

            // REMOVE ANY QUESTIONS WITH "-ACROSS" or "-DOWN" IN THE QUESTION
        }
    },
    DICTIONARY("dictionary.csv") {
        override fun parseQuestion(line: Array<String>): DictionaryQuestion {
            return DictionaryQuestion(line[2], line[0])
        }
    };

    companion object {
        fun loadQuestions(vararg dataSources: DataSource): MutableList<Question> {
            val questions = mutableListOf<Question>()
            for (dataSource in dataSources) {
                val inputStream = javaClass.classLoader.getResourceAsStream(dataSource.fileName)
                    ?: throw FileNotFoundException("File not found: $dataSource.fileName")
                val reader = CSVReader(inputStream.reader())
                reader.forEach { line ->
                    val question = dataSource.parseQuestion(line)
                    questions.add(question)
                }
            }
            questions.shuffle()
            return questions
        }

        fun parseDate(date: String): GregorianCalendar {
            val parts = date.split("/")
            val month = parts[0].toInt()
            val day = parts[1].toInt()
            val year = parts[2].toInt()
            return GregorianCalendar(year, month - 1, day)
        }
    }

    abstract fun parseQuestion(line: Array<String>): Question
}

abstract class Question(open val question: String, open val answer: String) {
    open var guesses = 0
    open var answeredCorrectly: Boolean? = null
    val answered: Boolean
        get() = answeredCorrectly != null

    abstract fun guess(input: String): Boolean

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d yyyy")
    }
}

class DictionaryQuestion(override val question: String, override val answer: String) : Question(question, answer) {
    override fun guess(input: String): Boolean {
        answeredCorrectly = input.equals(answer, ignoreCase = true)
        return answeredCorrectly!!
    }

    enum class Type(val shorthand: String) {
        NOUN("n."), VERB("v."), ADJECTIVE("a."), ADVERB("adv."), PRONOUN("p."), PREPOSITION("prep."), CONJUNCTION("conj."), INTERJECTION(
            "interj."
        ),
        PAST_PARTICIPLE("p. p."), PRESENT_PARTICIPLE("p. pr."), POSSESSIVE_ADJECTIVE("p. a."), IMPERATIVE("imp."), TRANSITIVE_VERB(
            "v. t."
        ),
        INTRANSITIVE_VERB("v. i."), PLURAL_NOUN("n. pl."), PLURAL("pl."), VERBAL_NOUN("vb. n."), PREFIX("A prefix."), OTHER(
            " "
        )
    }
}

class CrosswordQuestion(
    val date: GregorianCalendar,
    override val question: String,
    override val answer: String,
    override var answeredCorrectly: Boolean? = null,
    override var guesses: Int = 0
) : Question(question, answer) {
    val formattedDate: String
        get() = date.toZonedDateTime().format(formatter)

    override fun guess(input: String): Boolean {
        answeredCorrectly = input.equals(answer, ignoreCase = true)
        return answeredCorrectly ?: false
    }
}