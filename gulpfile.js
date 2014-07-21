var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var rev = require('gulp-rev');
var filesize = require('gulp-filesize');
var ngmin = require('gulp-ngmin');
var minifycss = require('gulp-minify-css');
var less = require('gulp-less');
var useref = require('gulp-useref');
var gulpif = require('gulp-if');

gulp.task('default', function(){
  gulp.src('src/main/webapp/index.html')
      .pipe(useref.assets())
      .pipe(gulpif('*.js', ngmin()))
      .pipe(gulpif('*.js', uglify()))
      .pipe(useref.restore())
      .pipe(useref())
      .pipe(gulp.dest('target/dist'));
});

gulp.on('err', function (err) {
  throw err;
});