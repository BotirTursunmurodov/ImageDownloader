# ImageDownloader
Download image from any url

> Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
> Step 2. Add the dependency
```gradle
dependencies {
	        implementation 'com.github.BotirTursunmurodov:ImageDownloader:1.0.0'
	}
  ```
  
> Step 3. Write the code for use
```gradle
ImageDownloader.downloadImage(context, "your image url")
```
