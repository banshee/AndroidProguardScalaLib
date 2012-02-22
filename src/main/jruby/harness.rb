$LOAD_PATH << "/Users/james/workspace/AndroidProguardScala/lib"
$LOAD_PATH << "/Users/james/workspace/AndroidProguardScala/src/main/jruby"
require "asm-3.3.1.jar"
require "proguard-base-4.6.jar"
require 'proguard_cache'
require 'jruby_environment_setup'
require 'ostruct'

base = "/Users/james/runtime-EclipseApplicationwithEquinoxWeaving/AndroidTest"
input_directories = [base + "/bin"]

class Lgr
  def logMsg s
    puts s
  end
end

p = ProguardCacheRuby.new
args = { "classFiles" =>  input_directories,
  "proguardDefaults" => "# some defaults go here\n-ignorewarnings\n-libraryjars /Users/james/src/android/prebuilt/sdk/9/android.jar",
  "proguardAdditionsFile" => base + '/proguard_cache/proguard_additions.conf',
  "proguardProcessedConfFile" => base + '/proguard_cache/proguard.conf',
  "workspaceDir" => "/Users/james/runtime-EclipseApplicationwithEquinoxWeaving",
  "cachedJar" => base + '/proguard_cache/scala.CKSUM.jar',
  "outputJar" => base + '/lib/scala_proguard.jar',
  "confDir" => base + "/proguard_cache_conf",
  'logger' => Lgr.new,
  "cacheDir" => base + "/proguard_cache",
  "scalaLibraryJar" => '/Users/james/lib/scala-2.9.1.final/lib/scala-library.jar'
}
p.build_dependency_files_and_final_jar OpenStruct.new(args)
