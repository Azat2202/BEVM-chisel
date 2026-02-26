package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MemoryTest extends AnyFreeSpec with Matchers with ChiselSim{
  "Memory should be read and written" in
    simulate(new Memory){ dut =>
      dut.input.write.poke(true.B)
      dut.input.address.poke(0x7FF.U)
      dut.input.data.poke(0xFFFF.U)

      dut.clock.step()

      dut.input.write.poke(false.B)
      dut.input.address.poke(0x7FF.U)

      dut.clock.step()
      dut.output.data.expect(0xFFFF.U)
    }

}
