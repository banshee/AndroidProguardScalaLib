require 'java'

#$LOAD_PATH << "/Users/james/workspace/AndroidProguardScala/jruby"
#$LOAD_PATH << "/Users/james/src/jruby/lib/ruby/1.8/"

java_package 'com.restphone.androidproguardscala.jruby'

class JrubyEnvironmentSetup
  java_signature 'void addJrubyJarfile(String pathToJrubyCompleteJarfile)'
  def self.add_jruby_jarfile jruby_complete_jarfile
    require 'jruby'
    require jruby_complete_jarfile
    ruby_paths =
    if JRuby.runtime.is1_9
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
    require f.basename
    puts %Q{require "#{f.parent}"}
    puts %Q{require "#{f.basename}"}
  end

  java_signature 'void addIvyDirectoryToLoadPath(String dir)'

  def self.add_ivy_directory_to_load_path dir
    require 'pathname'
    all_jars = Dir.glob(dir.to_s + "/**/*.jar")
    all_jars.each do |j|
      f = Pathname.new j
      case j
      when /asm-3.3.1.jar/, /proguard-base-4.6.jar/
        $LOAD_PATH << f.parent
      end
    end
  end
end
