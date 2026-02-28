package gcd

import chisel3._

class ResultAttributesBlockOutput extends Bundle {
  val data = SInt(16.W)
}

trait ResultAttributesBlockFlags { this: Bundle =>
  val SETV = Bool()
  val SETC = Bool()
  val SETZ = Bool()
}

class ResultAttributesBlockFlagsImpl extends Bundle with ResultAttributesBlockFlags {}

class NZVCRegister extends Bundle {
  val N = Bool()
  val Z = Bool()
  val V = Bool()
  val C = Bool()
}

class ResultAttributesBlock extends Module {
  val input      = IO(Input(new CommutatorOutput()))
  val flags      = IO(Input(new ResultAttributesBlockFlagsImpl()))
  val nzvcInput  = IO(Input(new NZVCRegister()))
  val nzvcOutput = IO(Output(new NZVCRegister()))
  val output     = IO(Output(new ResultAttributesBlockOutput()))

  nzvcOutput.N := input.data < 0.S
  nzvcOutput.Z := nzvcInput.Z
  nzvcOutput.V := nzvcInput.V
  nzvcOutput.C := nzvcInput.C
  output.data  := input.data

  when(flags.SETV) {
    nzvcOutput.V := input.C ^ input.C14
  }
  when(flags.SETC) {
    nzvcOutput.C := input.C
  }
  when(flags.SETZ) {
    nzvcOutput.Z := input.data === 0.S
  }
}
