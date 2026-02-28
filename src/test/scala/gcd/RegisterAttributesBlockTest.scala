package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class RegisterAttributesBlockTest extends AnyFreeSpec with Matchers with ChiselSim {

  "Commutator should set N for positive" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.input.data.poke(0x0bcd.S)
      dut.nzvcOutput.N.expect(false.B)
    }

  "Commutator should set N for negative" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.input.data.poke(0xfbcd.S)
      dut.nzvcOutput.N.expect(true.B)
    }

  "Commutator should set Z for zero" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.Z.poke(true.B)
      dut.input.data.poke(0x0000.S)
      dut.flags.SETZ.poke(true.B)
      dut.nzvcOutput.Z.expect(true.B)
    }

  "Commutator should set Z for non zero" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.Z.poke(true.B)
      dut.input.data.poke(0xabcd.S)
      dut.flags.SETZ.poke(true.B)
      dut.nzvcOutput.Z.expect(false.B)
    }

  "Commutator should not set Z whithout flag" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.Z.poke(true.B)
      dut.input.data.poke(0xabcd.S)
      dut.flags.SETZ.poke(false.B)
      dut.nzvcOutput.Z.expect(true.B)
    }

  "Commutator should set V for overflow" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.V.poke(true.B)
      dut.input.C14.poke(true.B)
      dut.input.C.poke(true.B)
      dut.flags.SETV.poke(true.B)
      dut.nzvcOutput.V.expect(false.B)
    }

  "Commutator should not set V for non overflow" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.V.poke(true.B)
      dut.input.C14.poke(true.B)
      dut.input.C.poke(false.B)
      dut.flags.SETV.poke(true.B)
      dut.nzvcOutput.V.expect(true.B)
    }

  "Commutator should not set V whithout flag" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.V.poke(false.B)
      dut.input.C14.poke(true.B)
      dut.input.C.poke(false.B)
      dut.flags.SETV.poke(false.B)
      dut.nzvcOutput.V.expect(false.B)
    }

  "Commutator should set C for carry" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.C.poke(true.B)
      dut.input.C.poke(false.B)
      dut.flags.SETC.poke(true.B)
      dut.nzvcOutput.C.expect(false.B)
    }

  "Commutator should not set C for non carry" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.C.poke(true.B)
      dut.input.C.poke(false.B)
      dut.flags.SETC.poke(true.B)
      dut.nzvcOutput.C.expect(false.B)
    }

  "Commutator should not set C without flag" in
    simulate(new ResultAttributesBlock) { dut =>
      dut.nzvcInput.C.poke(true.B)
      dut.input.C.poke(false.B)
      dut.flags.SETC.poke(false.B)
      dut.nzvcOutput.C.expect(true.B)
    }
}
