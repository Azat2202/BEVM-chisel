package gcd

import chisel3._

import scala.util.Using

object utils {
  def loadMemoryFromResource(
      fileName: String,
      memory: SyncReadMem[UInt],
      reset: Reset
  ): WhenContext = loadMemoryFromResourceImpl(fileName, reset) { (i, value) =>
    memory(i) := value
  }

  def loadMemoryFromResource(
      fileName: String,
      memory: Mem[UInt],
      reset: Reset
  ): WhenContext = loadMemoryFromResourceImpl(fileName, reset) { (i, value) =>
    memory(i) := value
  }

  private def loadMemoryFromResourceImpl(fileName: String, reset: Reset)(
      write: (Int, UInt) => Unit
  ): WhenContext = {
    val memContent = Using(scala.io.Source.fromFile(fileName)) {
      _.getLines().map(line => BigInt(line, 16)).toSeq
    }.get

    when(reset.asBool) {
      if (memContent.nonEmpty) {
        for (i <- memContent.indices)
          write(i, memContent(i).asUInt)
      }
    }
  }
}
