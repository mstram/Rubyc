# DisplayCircuit: rubyc class for controlling a display chip. 

require 'java'
import 'org.tal.rubychip.RubyCircuit'
import 'org.tal.redstonechips.wireless.Transmitter'
import 'org.tal.redstonechips.util.BitSet7'
import 'org.tal.redstonechips.util.BitSetUtils'

class DisplayController
  attr_reader :width, :height, :x_len, :y_len, :color_len, :tx
  
  def initdisplay(width, height, rbcircuit, channel)
    @rbcircuit = rbcircuit
    @width = width
    @height = height

    @x_len = calc_bit_length(width)
    @y_len = calc_bit_length(height)
    
    if channel!=nil
      @tx = Transmitter.new()
      @tx.init rbcircuit.sender, channel, 1, rbcircuit.circuit
      @color_len = 4
    else
      @color_len = rbcircuit.outputs.length - @x_len - @y_len - 1
      if @color_len<=0
        rbcircuit.error "Expecting at least #{@x_len+@y_len+1} outputs."
        return false        
      end
    end

    rbcircuit.info "Display controller #{width}x#{height}. #{@x_len.to_s} x bits. #{@y_len.to_s} y bits. #{@color_len.to_s} color bits."

    true
  end

  def paint(x, y, color)
    if @tx!=nil
      xbits = BitSetUtils.intToBitSet x, @x_len
      ybits = BitSetUtils.intToBitSet y, @y_len
      cbits = BitSetUtils.intToBitSet color, @color_len
      
      bits = BitSet7.new()
      for i in 0..@x_len
        bits.set i, xbits.get(i)
      end
      
      for i in 0..@y_len
        bits.set @x_len+i, ybits.get(i)
      end
      
      for i in 0..@color_len
        bits.set @x_len+@y_len+i, cbits.get(i)
      end
      
      @tx.send bits, 0, @x_len+@y_len+@color_len
    else
      @rbcircuit.send 1, @x_len, x
      @rbcircuit.send 1+@x_len, @y_len, y
      @rbcircuit.send 1+@x_len+@y_len, @color_len, color
      @rbcircuit.send 0, true
      @rbcircuit.send 0, false
    end
  end
  
  def clear(color)
    for x in 0..@width-1
      for y in 0..@height-1
        paint x, y, color
      end
    end    
  end
  
  def calc_bit_length(maxnum)
    return (Math.log(maxnum)/Math.log(2)).ceil;
  end
end
