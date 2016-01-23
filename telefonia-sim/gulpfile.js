var gulp = require('gulp');
var babel = require('gulp-babel');
var babelRegister = require('babel/register');
var mocha = require('gulp-mocha');
var flow = require('gulp-flowtype');
var connect = require('gulp-connect');
var browserify = require('browserify');
var babelify = require('babelify');
var fs = require('fs');

gulp.task('browserify', function () {
  browserify("app/app.jsx", { debug: true })
    .transform(babelify)
    .bundle()
    .on("error", function (err) { console.log("Error : " + err.message); })
    .pipe(fs.createWriteStream("app/app.js"));
});

gulp.task('mocha', function() {
  return gulp.src('test/*.js', { read: false })
        .pipe(mocha({
          js: babelRegister,
          reporter: 'dot'
        }));
});

gulp.task('flow', function() {
  return gulp.src(['src/**/*.js', 'test/*.js'])
    .pipe(flow({
      all: false,
      weak: false,
      killFlow: false,
      beep: true,
      abort: false
    }));
});

gulp.task('connect', function() {
  connect.server({
    root: 'app/',
    livereload: true
  });
});

gulp.task('watch', ['browserify', 'mocha', 'connect'], function() {
  gulp.watch(['app/*.jsx', 'app/js/*.js', 'src/*.js'], ['browserify']);
  gulp.watch(['src/**/*.js', 'test/**/*.js'], ['mocha']);
  // gulp.watch(['src/**/*.js'], ['flow']);
});

gulp.task('default', ['flow', 'browserify', 'mocha']);
