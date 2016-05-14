import org.scalatest.Matchers
import org.scalatest.FunSpec

class TestProcessor(list: Seq[String]) extends Processor {
  def jsonLines = list.toStream
}

class JsonTest extends FunSpec with Matchers {
  describe("counter") {
    def t(name: String, s: Seq[String], words: Int,
      events: Map[String, Int]) = {
      it(name){
        val proc = new TestProcessor(s)
        proc.run
        val (actual_words, actual_events) = proc.stats
        assert(actual_words==words)
        assert(actual_events==events)
      }
    }
    t("basic", Seq(), 0, Map())
    t("json", Seq(
        s"""{"event_type": "foo", "data": "lorem"}""",
        s"""{"event_type": "bar", "data": "lorem"}"""
      ), 1, Map("foo"->1, "bar"->1))
    t("corrupt", Seq(
        s"""{"event_type": "foo", "data": "lorem"}""",
        s"""{"xxxxx_type" "bar", "data": "lorem"}"""
      ), 1, Map("foo"->1))
  }
}
