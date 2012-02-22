require 'java'

java_package 'com.restphone.androidproguardscala.jruby'

# This file is normally compiled to Java and called from Java or Scala - it's not used
# from Ruby.

# Notice that in this file we can't do require at the top level.  This file contains
# code that sets up the runtime environment, including things like the LOAD_PATH that
# you need to use require.

class JrubyEnvironmentSetup
  java_signature 'void addJrubyJarfile(String pathToJrubyCompleteJarfile)'
  def self.add_jruby_jarfile jruby_complete_jarfile
    require 'jruby'
    require jruby_complete_jarfile
    ruby_paths = if JRuby.runtime.is1_9
      %w{ site_ruby/1.9 site_ruby/shared site_ruby/1.8 1.9 }
    else
      %w{ site_ruby/1.8 site_ruby/shared 1.8 }
    end
    ruby_paths.each do |path|
      full_path = jruby_complete_jarfile + "!/META-INF/jruby.home/lib/ruby/#{path}"
      full_path = "META-INF/jruby.home/lib/ruby/#{path}"
      $LOAD_PATH << full_path unless $LOAD_PATH.include?(full_path)
    end
  end

  java_signature 'void addToLoadPath(String file)'

  def self.add_to_load_path file
    require 'pathname'
    $LOAD_PATH << file
  end

  java_signature 'void addJarToLoadPathAndRequire(String jarfileFullPath)'

  def self.add_jar_to_load_path_and_require jarfile
    require 'pathname'
    f = Pathname.new jarfile.to_s
    $LOAD_PATH << f.parent
  end
end
