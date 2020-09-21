/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */


// load gulp
var gulp = require('gulp');
var $ = require('gulp-load-plugins')();

gulp.task('jshint', function(){
  gulp.src('src/main/webapp/scripts/**/*.js')
      .pipe($.jshint())
      .pipe($.jshint.reporter('jshint-stylish'));
});

gulp.task('lessc', function(){
  return gulp.src('src/main/webapp/style/less/*')
      .pipe($.less())
      .pipe(gulp.dest('target/gulp/style/css/'));
});

gulp.task('build-template-cache', function(){
  var opts = {
    filename: 'universeadm.tpl.js',
    module: 'universeadm',
    root: 'views/'
  };
  return gulp.src('src/main/webapp/views/**/*.html')
      .pipe($.angularTemplatecache(opts))
      .pipe(gulp.dest('target/gulptmp/scripts'));
});

gulp.task('default', gulp.series('lessc', 'build-template-cache', function(){
  // copy index.html for debugging purposes
  gulp.src('src/main/webapp/*.html')
      .pipe($.rename({suffix: '-debug'}))
      .pipe(gulp.dest('target/gulp'));

  // concat, compress and rename resources from index.html
  return gulp.src('src/main/webapp/index.html')
      .pipe($.useref({searchPath: '{target/gulp,target/gulptmp,src/main/webapp}'}))
      .pipe($.filesize())
      .pipe($.if('*.js', $.ngAnnotate()))
      .pipe($.if('*.js', $.uglify()))
      .pipe($.if('*.css', $.minifyCss()))
      .pipe($.if('!*.html', $.rev()))
      .pipe($.if('!*.html', $.rename({suffix: '.min'})))
      .pipe($.revReplace())
      .pipe($.filesize())
      .pipe(gulp.dest('target/gulp'));
}));

gulp.on('err', function (err) {
  throw err;
});
