// load gulp
var gulp = require('gulp');
var gutil = require('gulp-util');

// load plugins
var uglify = require('gulp-uglify');
var rev = require('gulp-rev');
var filesize = require('gulp-filesize');
var ngannotate = require('gulp-ng-annotate');
var minifycss = require('gulp-minify-css');
var less = require('gulp-less');
var useref = require('gulp-useref');
var gulpif = require('gulp-if');
var size = require('gulp-size');
var rename = require('gulp-rename');
var revReplace = require('gulp-rev-replace');
var templateCache = require('gulp-angular-templatecache');

gulp.task('lessc', function(){
  gutil.log('run lessc');
  return gulp.src('src/main/webapp/style/less/*')
      .pipe(less())
      .pipe(gulp.dest('target/gulp/style/css/'));
});

gulp.task('build-template-cache', function(){
  gutil.log('run build-template-cache');
  var opts = {
    filename: 'usermgm.tpl.js',
    module: 'usermgm',
    root: 'views/'
  };
  return gulp.src('src/main/webapp/views/**/*.html')
      .pipe(templateCache(opts))
      .pipe(gulp.dest('target/gulptmp/scripts'))
});

gulp.task('default', ['lessc', 'build-template-cache'], function(){
  gutil.log('run default');

  // copy index.html for debugging purposes
  gulp.src('src/main/webapp/*.html')
      .pipe(rename({suffix: '-debug'}))
      .pipe(gulp.dest('target/gulp'));

  // concat, compress and rename resources from index.html
  gulp.src('src/main/webapp/index.html')
      .pipe(useref.assets({searchPath: '{target/gulp,target/gulptmp,src/main/webapp}'}))
      .pipe(size())
      .pipe(gulpif('*.js', ngannotate()))
      .pipe(gulpif('*.js', uglify()))
      .pipe(gulpif('*.css', minifycss()))
      .pipe(rev())
      .pipe(rename({suffix: '.min'}))
      .pipe(useref.restore())
      .pipe(useref())
      .pipe(revReplace())
      .pipe(gulp.dest('target/gulp'));
});

gulp.on('err', function (err) {
  throw err;
});
