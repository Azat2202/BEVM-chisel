package gcd

import chisel3._

class ResultAttributesBlockOutput extends Bundle {
  val N    = Bool()
  val Z    = Bool()
  val V    = Bool()
  val C    = Bool()
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
  val input  = IO(Input(new CommutatorOutput()))
  val flags  = IO(Input(new ResultAttributesBlockFlagsImpl()))
  val nzvc   = IO(Input(new NZVCRegister()))
  val output = IO(Output(new ResultAttributesBlockOutput()))

  output.N    := input.data < 0.S
  output.Z    := nzvc.Z
  output.V    := nzvc.V
  output.C    := nzvc.C
  output.data := input.data

  when(flags.SETV) {
    output.V := input.C ^ input.C14
  }
  when(flags.SETC) {
    output.C := input.C
  }
  when(flags.SETZ) {
    output.Z := input.data === 0.S
  }
}
