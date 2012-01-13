# http://homepages.cwi.nl/~tromp/c4/fhour.html

require 'java'
import 'org.tal.rubychip.RubyCircuit'
require 'lib/displaycontroller'
require 'c4animation'

class Connect4 < RubyCircuit
  def init
    @board_disp = DisplayController.new()
    @status_disp = DisplayController.new()
    
    return false unless @board_disp.initdisplay 7, 6, self, args[0]
    return false unless @status_disp.initdisplay 10, 1, self, args[1]
    
    
    #input configuration
    @power_input = 4
    @clock_input = 5
    @clock_trigger = 0
    @player1_input = 0
    @player2_input = 6
    @p_color = [4, 14]
    
    @status_disp.clear 0
    @board_disp.clear 0
    
    turnon       
    
    info "Connect4 initialized."
    
    true
  end

  def input(idx, state)
    if idx==@power_input
      if state
        turnon
      else turnoff
      end
    elsif idx==@player1_input && state && @on # player1 move
      play 0, bits_to_i(@player1_input+1, 3, inputs)
    elsif idx==@player2_input && state && @on # player2 move
      play 1, bits_to_i(@player2_input+1, 3, inputs)
    elsif idx==@clock_input && state 
      clock
    end
  end
  
  def turnon
    @on = true
    @cur_player = 0 # player1 starts
    @board = Board.new()    
    animate Animation::NEW_TURN, @p_color[@cur_player]
    alert "new game"
  end
  
  def turnoff
    @on = false
    @board_disp.clear 0
    @status_disp.clear 0
    @stop_animation = true
    alert "game is off"
  end
  
  def play(player, column)
    unless @cur_player==player
      alert "It's not your turn."
    else      
      #begin
        cell = @board.drop player, column
        alert "Player #{player+1} drops disc into column #{column+1}"
        @board_disp.paint column, 6-cell-1, @p_color[player]
        
        if @board.did_win player
          alert "Player #{player+1} won!"
          animate Animation::WIN, @p_color[@cur_player]
        else next_turn
        end        
      #rescue Exception => e
      #  alert e.message
      #end
    end    
  end
 
  def next_turn
    if @cur_player==0
      @cur_player = 1
    else @cur_player = 0
    end
    animate Animation::NEW_TURN, @p_color[@cur_player]
    alert "Player #{@cur_player+1} turn."
  end
  
  def animate type, color
    @on = false
    @cur_animation = Animation.new(@status_disp, color, type)
    send @clock_trigger, true
  end
    
  def clock
    unless @cur_animation==nil
      if @stop_animation || !@cur_animation.next_frame 
        send @clock_trigger, false
        @on = true
        @stop_animation = false
      end
    end
  end
  
  def alert msg
    debug(msg)
  end    
end

class Board 
  def initialize()
    @gridp1 = Array.new(7) {Array.new(7) {false}}
    @gridp2 = Array.new(7) {Array.new(7) {false}}
    @highest_cell = Array.new(7) {0}
  end
  
  def drop(player, col)
    if col>=7 || col<0
      raise "Column #{col+1} is out of bounds!"
    else
      cell = @highest_cell[col]
      if cell >= 6
        raise "Column #{col+1} is full."
      else      
        @highest_cell[col] = cell+1
        if (player==0)
          @gridp1[col][cell] = true
        else 
          @gridp2[col][cell] = true
        end
        return cell
      end      
    end
  end
  
  def did_win(player)
    if (player==0) 
      board = grid_to_i(@gridp1.flatten)
    else 
      board = grid_to_i(@gridp2.flatten)
    end
    
    y = board & (board >> 6)    
    if y & (y >> 2 * 6) > 0     # check \ diagonal
      return true
    end
    
    y = board & (board >> 7);
    if y & (y >> 2 * 7) > 0     # check horizontal
      return true;
    end
    
    y = board & (board >> 8);
    if y & (y >> 2 * 8) > 0     # check / diagonal
      return true;
    end
    y = board & (board >> 1);
    if y & (y >> 2) > 0         # check vertical
      return true;
    end

    return false
  end
  
  def grid_to_i(grid)
    val = 0
    for i in 0..49
      if grid[i]
        val += 2**i
      end
    end
    
    return val
  end
end

Connect4.new()