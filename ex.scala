trait Processor {
  val m = scala.collection.mutable.Map[String, Int]()
  val words = scala.collection.mutable.Set[String]()

  def jsonLines: Stream[String]

  def process(st: Stream[String]) = {
    for (s <- st;
         j <- jawn.ast.JParser.parseFromString(s);
         et <- j.get("event_type").getString;
         data <- j.get("data").getString) {
      this.synchronized {
        m.update(et, m.getOrElse(et, 0)+1)
        words.add(data)
      }
    }
  }

  def stats = { this.synchronized { (words.size, m.toMap) } }

  def run = process(jsonLines)
}

class ExecProcessor(execPath: String) extends Processor {
  def jsonLines(): Stream[String] = {
    import scala.sys.process._
    val cmd = Seq(execPath)
    cmd.lineStream
  }
}

class ProcessorHandler(proc: Processor) extends unfiltered.filter.Plan {
  import unfiltered.request._
  import unfiltered.response._
  import scala.collection.mutable
  def intent = {
    case Path(Seg("stats.json" :: Nil)) =>
      import jawn.ast._
      val (words, events) = proc.stats
      val jevents: mutable.Map[String, JValue] = mutable.Map(
        events.mapValues(v=>JNum(v)).toSeq: _*)
      val jres = JObject(mutable.Map("words"->JNum(words),
        "values"->JObject(jevents)))
      JsonContent ~> ResponseString(FastRenderer.render(jres))
  }
}

object App {
  def main(args: Array[String]) = {
    val proc = new ExecProcessor("./exec")
    new Thread(new Runnable(){ def run(){ proc.run } }).start()
    val http = unfiltered.jetty.Server.local(8080)
    http.plan(new ProcessorHandler(proc)).run()
    // our stream never ends, force exit the program here for convenience
    System.exit(0)
  }
}
