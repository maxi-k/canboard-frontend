compile-sass:
	sass --watch src/sass/site.scss:resources/public/css/site.css

compile-sass-prod:
	sass src/sass/site.scss:resources/public/css/site.min.css --style compressed
