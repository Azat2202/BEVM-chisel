package gcd

import chisel3._

import scala.util.Using

object utils {
  def loadMemoryFromResource(
      fileName: String,
      memory: SyncReadMem[UInt],
      reset: Reset
  ): WhenContext = {
    // https://github.com/chipsalliance/chisel/issues/4340
    val memContent = Using(scala.io.Source.fromFile(fileName)){
      _.getLines().map(line => BigInt(line, 16)).toSeq
    }.get

    when(reset.asBool) {
      if(memContent.nonEmpty) {
        for (i <- memContent.indices) {
          memory(i) := memContent(i).asUInt
        }
      }
    }
  }
}
