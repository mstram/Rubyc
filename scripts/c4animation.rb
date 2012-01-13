require 'java'
import 'org.tal.rubychip.RubyCircuit'
require 'lib/displaycontroller'

class Animation 
  NEW_TURN = 0
  WIN = 1
  DROP_DISC = 2
  
  def initialize(display, color, type)
    @disp = display
    @color = color
    @type = type 
    @frame = 0
    @idx = 0
  end
  
  def next_frame
    case @type
      when Animation::NEW_TURN
        if @frame<2
          animate_turn
          else return false      
        end
      when Animation::WIN
        animate_win
      when Animation::DROP_DISC
        animate_drop
    end
    @frame += 1
    
    true
  end
  
  def animate_win
    @disp.clear rand(8)+5
    @disp.paint @idx, 0, @color
    @idx += 1
    if @idx>=@disp.width
      @idx = 0
    end
  end
  
  def animate_turn
    unless @frame%2==0
      @disp.clear @color
    else @disp.clear 7
    end    
  end
end