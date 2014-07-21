var gulp = require('gulp');
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

gulp.task('useref', function(){
  gulp.src('src/main/webapp/index.html')
      .pipe(useref.assets({searchPath: '{target/gulp,src/main/webapp}'}))
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

gulp.task('lessc', function(){
  gulp.src('src/main/webapp/style/less/*')
      .pipe(less())
      .pipe(gulp.dest('target/gulp/style/css/'));
});

gulp.task('default', ['lessc', 'useref']);

gulp.on('err', function (err) {
  throw err;
});