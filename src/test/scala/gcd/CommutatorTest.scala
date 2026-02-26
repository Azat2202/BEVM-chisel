package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class CommutatorTest extends AnyFreeSpec with Matchers with ChiselSim {

  "Commutator should perform direct connection" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U.asSInt)
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(false.B)

      dut.flags.HTOH.poke(true.B)
      dut.flags.LTOL.poke(true.B)

      dut.output.data.expect(0xabcd.U.asSInt)
      dut.output.C.expect(true.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform SWAP" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U.asSInt)
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.HTOL.poke(true.B)
      dut.flags.LTOH.poke(true.B)

      dut.output.data.expect(0xcdab.U.asSInt)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform EXTEND SIGH" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U.asSInt)
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SEXT.poke(true.B)
      dut.flags.LTOL.poke(true.B)

      dut.output.data.expect(0xffcd.U.asSInt)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform ASL (x * 2)" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U(16.W).asSInt) // 1010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHLT.poke(true.B)

      dut.output.data.expect(0x579a.U(16.W).asSInt) // 0101 0111 1001 1010
      dut.output.C.expect(true.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform ROL" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U(16.W).asSInt) // 1010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHLT.poke(true.B)
      dut.flags.SHL0.poke(true.B)

      dut.output.data.expect(0x579b.U(16.W).asSInt) // 0101 0111 1001 1011
      dut.output.C.expect(true.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform ROR with 1 in C" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U.asSInt) // 1010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHRT.poke(true.B)
      dut.flags.SHRF.poke(true.B)

      dut.output.data.expect(0xd5e6.U.asSInt) // 1101 0101 1110 0110
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }

  "Commutator should perform ROR with 0 in C" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U(16.W).asSInt) // 1010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(false.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHRT.poke(true.B)
      dut.flags.SHRF.poke(true.B)

      dut.output.data.expect(0x55e6.U(16.W).asSInt) // 0101 0101 1110 0110
      dut.output.C.expect(true.B)
      dut.output.C14.expect(false.B)
    }

  "Commutator should perform ASR for negative number" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0xabcd.U.asSInt) // 1010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHRT.poke(true.B)

      dut.output.data.expect(0xd5e6.U.asSInt) // 1101 0101 1110 0110
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }

  "Commutator should perform ASR for positive number" in
    simulate(new Commutator) { dut =>
      dut.input.data.poke(0x2bcd.U(16.W).asSInt) // 0010 1011 1100 1101
      dut.input.C.poke(true.B)
      dut.input.C0.poke(true.B)
      dut.input.C14.poke(true.B)

      dut.flags.SHRT.poke(true.B)

      dut.output.data.expect(0x15e6.U(16.W).asSInt) // 0001 0101 1110 0110
      dut.output.C.expect(true.B)
      dut.output.C14.expect(false.B)
    }

}
