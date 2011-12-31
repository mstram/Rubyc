Rubyc
-------
Rubyc is a [RedstoneChips](http://eisental.github.com/RedstoneChips) chip library containing `rubyc` - a programmable chip that can run ruby scripts.

Compile
---------
You need to have Maven installed (http://maven.apache.org). 

Once installed run `mvn clean package install` from the project root folder.

Install
-------
- Download and install [RedstoneChips](http://eisental.github.com/RedstoneChips).
- Copy RubycLibrary-beta.jar into your craftbukkit /plugins folder.
- Create the plugin data folder plugins/Rubyc.
- Download the JRuby runtime version 1.6.x from http://jruby.org/download (get JRuby 1.6.x Binary as tar.gz or zip).
- Extract the file jruby.jar in the lib folder from that archive and copy it into plugins/Rubyc.
- Optionally, copy all script files in the examples/ folder into Rubyc data folder (plugins/Rubyc).

Usage
-----
The chip library adds a `rubyc` chip type. 
The first sign argument is the script name (without .rb extension).
If there's no script by that name it will create a default script in the Rubyc folder using the new script name. 
You can edit the script file and reload the changes by using /rcreset on the rubyc chip. 

For example, to make a rubyc using the not.rb script, type these lines into the chip sign:

1 `rubyc`
2 `not`

This script requires at least 1 input/output pair.

Contribute
----------
- If you have any feature suggestions, bug reports or other issues, please use the [issue tracker](https://github.com/eisental/Rubyc/issues).
- We happily accept contributions. The best way to do this is to fork Rubyc on GitHub, add your changes, and then submit a pull request. We'll try it out, and hopefully merge it into Rubyc.

Changelog
---------

### 31/12/11
- Uploaded initial sources