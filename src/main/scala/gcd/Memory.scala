package gcd

import chisel3._

class MemoryInput extends Bundle {
  val write   = Bool()
  val address = UInt(11.W)
  val data    = UInt(16.W)
}

class MemoryOutput extends Bundle {
  val data = UInt(16.W)
}

class Memory extends Module {
  val input  = IO(Input(new MemoryInput()))
  val output = IO(Output(new MemoryOutput()))

  val mem: SyncReadMem[UInt] = SyncReadMem(2048, UInt(16.W)) // 2048 is size of 7FF memory
  when(input.write) {
    mem.write(input.address, input.data)
  }
  output.data := mem.read(input.address)
}
