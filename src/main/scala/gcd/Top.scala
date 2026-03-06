package gcd

import chisel3._
import chisel3.simulator.EphemeralSimulator

class Flags extends Bundle with AluFlags with CommutatorFlags with RegisterSetFlags with ResultAttributesBlockFlags

class TopInput extends Bundle {
  val writeToRegisters = Bool()
  val data             = UInt(16.W)
}

class TopOutput extends Bundle {
  val data = UInt(16.W)
}

/** Module is connecting all non-managment modules
  */
class Top(memoryFileName: String = "") extends Module {
  val alu: Alu                                     = Module(new Alu())
  val commutator: Commutator                       = Module(new Commutator())
  val resultAttributesBlock: ResultAttributesBlock = Module(new ResultAttributesBlock())
  val memory: Memory                               = Module(new Memory(memoryFileName))
  val registerSet: RegisterSet                     = Module(new RegisterSet())
  val flags                                        = IO(Input(new Flags()))
  val topInput                                     = IO(Input(new TopInput()))
  val topOutput                                    = IO(Output(new TopOutput()))

  alu.flags                   := flags
  commutator.flags            := flags
  resultAttributesBlock.flags := flags
  registerSet.flags           := flags

  alu.input := registerSet.output

  commutator.input := alu.output

  resultAttributesBlock.input     := commutator.output
  resultAttributesBlock.nzvcInput := registerSet.nzvcOutput

  memory.input := registerSet.memoryInput

  registerSet.memoryOutput := memory.output
  registerSet.input        := resultAttributesBlock.output
  registerSet.nzvcInput    := resultAttributesBlock.nzvcOutput

  topOutput.data := resultAttributesBlock.output.data.asUInt

  when(topInput.writeToRegisters)(
    registerSet.input.data := topInput.data.asSInt
  )

}
