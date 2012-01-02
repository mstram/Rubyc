require 'java'
java_import 'org.tal.rubychip.RubyCircuit'

class Not < RubyCircuit
   def init
     setStateless true
     info "rubyc not"
     true
   end

   def input(idx, state)
      send idx, !state
   end
end

Not.new()