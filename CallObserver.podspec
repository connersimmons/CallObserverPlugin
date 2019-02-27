
  Pod::Spec.new do |s|
    s.name = 'CallObserver'
    s.version = '0.0.1'
    s.summary = 'CallKit phone call observer.'
    s.license = 'MIT'
    s.homepage = 'https://github.com/csimmonsbluebook/CallObserverPlugin.git'
    s.author = 'Conner Simmons'
    s.source = { :git => 'https://github.com/csimmonsbluebook/CallObserverPlugin.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end