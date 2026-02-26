package gcd

import chisel3._
import chisel3.util._
import chisel3.util.Fill._

class CommutatorOutput extends Bundle {
  val data = SInt(16.W)
  val C    = Bool()
  val C14  = Bool()
}

trait CommutatorFlags { this: Bundle =>
  val LTOL = Bool()
  val LTOH = Bool()
  val HTOL = Bool()
  val HTOH = Bool()
  val SEXT = Bool()
  val SHLT = Bool()
  val SHL0 = Bool()
  val SHRT = Bool()
  val SHRF = Bool()
}

class CommutatorFlagsImpl extends Bundle with CommutatorFlags {}

class Commutator extends Module {
  val input  = IO(Input(new AluOutput()))
  val flags  = IO(Input(new CommutatorFlagsImpl()))
  val output = IO(Output(new CommutatorOutput()))

  private val inputHigh  = input.data(15, 8)
  private val inputLow   = input.data(7, 0)
  private val outputHigh = Wire(UInt(8.W))
  private val outputLow  = Wire(UInt(8.W))
  outputHigh := 0.U
  outputLow  := 0.U

  output.C14 := false.B
  output.C   := false.B

  when(flags.HTOH) {
    outputHigh := inputHigh
    output.C   := input.C
    output.C14 := input.C14
  }
  when(flags.LTOL)(outputLow  := inputLow)
  when(flags.HTOL)(outputLow  := inputHigh)
  when(flags.LTOH)(outputHigh := inputLow)
  when(flags.SEXT)(outputHigh := Fill(8, inputLow(7)))
  when(flags.SHLT && flags.SHL0) {
    outputLow  := Cat(inputLow, input.C0)
    outputHigh := Cat(inputHigh, inputLow(7))
    output.C14 := inputHigh(6)
    output.C   := inputHigh(7)
  }
  when(flags.SHLT && !flags.SHL0) {
    outputLow  := inputLow << 1
    outputHigh := Cat(inputHigh, inputLow(7))
    output.C14 := inputHigh(6)
    output.C   := inputHigh(7)
  }
  when(flags.SHRT && flags.SHRF) {
    outputLow  := Cat(inputHigh(0), inputLow(7, 1))
    outputHigh := Cat(input.C0, inputHigh(7, 1))
    output.C   := inputLow(0)
    output.C14 := input.C0
  }
  when(flags.SHRT && !flags.SHRF) {
    outputLow  := Cat(inputHigh(0), inputLow(7, 1))
    outputHigh := Cat(inputHigh(7), inputHigh(7, 1))
    output.C   := inputLow(0)
    output.C14 := inputHigh(7)
  }

  output.data := Cat(outputHigh, outputLow).asSInt
}
