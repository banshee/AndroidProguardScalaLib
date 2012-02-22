require 'java'
require 'proguard_cache.rb'

java_package 'com.restphone.androidproguardscala.jruby'

class ProguardCacheJavaInterop
  def initialize *args
    @ruby_object = ProguardCacheRuby.new
  end
  
  def build_proguard_dependency_files *args
    @ruby_object.build_proguard_dependency_files *args
  end

  def run_proguard *args
    @ruby_object.run_proguard *args
  end

  def install_proguard_output *args
    @ruby_object.install_proguard_output *args
  end

  java_signature 'void clean_cache(String cacheDir)'

  def clean_cache cache_dir
    @ruby_object.clean_cache cache_dir
  end
end
