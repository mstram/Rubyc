require 'java'
java_import 'org.tal.rubychip.RubyCircuit'
require 'displaycircuit'

class Disp1 < DisplayCircuit  
  def init
    if !initdisplay
      return false
    end
    
    def_color1= 15
    def_color2 = 14

    info "disp demo 1 (args: width height [color1 color2])"

    @direction = 1    

    if args.length>2
      @color1 = args[2].to_i
    else @color1 = def_color1
    end
    
    if args.length>3
      @color2 = args[3].to_i
    else @color2 = def_color2
    end
    
    cleardisplay(@color1)

    @x = 0
    @y = 0
    @lastX = -1
    @lastY = -1
    
    true
  end

  def input(idx, state)
    # when getting clock
    if idx==0 
      if state
        step
      end      
    elsif idx<=4 
      @color1 = bits_to_i(1, 4, inputs)
      cleardisplay(@color1)
    elsif idx<=8
      @color2 = bits_to_i(5, 4, inputs)        
    end    
  end
  
  # advances the animation by one frame.
  def step
    stepy
    debug("x=#{@x} y=#{@y}")
    if @lastX>=0 
      paint @lastX, @lastY, @color1
    end
    
    paint @x, @y, @color2
    
    @lastX = @x
    @lastY = @y
  end
     
  def stepy
    @y += @direction
    if @y>=@dispHeight
      @y -= 1
      @direction = -@direction
      stepx
    else
      if @y<0
        @y = 0
        @direction = -@direction
        stepx
      end
    end
    
  end
  
  def stepx
    @x += 1
    if @x>=@dispWidth
      @x = 0
    end
  end  
end

Disp1.new()
