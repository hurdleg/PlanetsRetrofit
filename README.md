## Planets Retrofit ##
  Making HTTP requests with Square's Retrofit (https://square.github.io/retrofit/)

## Usage ##

1. First launch: list of planets by distance from Sun
    * Toast planet count: 8 (9 if Pluto exists on the web service)
2. **Menu > down-arrow Fetch planets from web service**
    * Toast planet count: 8 (9 if Pluto exists on the web service)
3. **Menu > + Create a new Planet (Pluto)**
    * Toast: Added Pluto
    * Observe: Pluto has been created on the web service, and the image is _No Image Found_
4. **Menu > pencil Update Pluto to hurdleg**
    * Toast: Updated Pluto
    * Observe: Pluto has been updated on the web service, and the image & name have been changed
5. **Menu > trash-can Delete Pluto**
    * Toast: Deleted Pluto
    * Observe: Pluto has been removed from the web service
6. **Menu > up-arrow Upload Image of Pluto**
    * _pre-condition_: Pluto must appear in the list before you can upload the image
    * Toast: Uploaded Image File of Pluto
    * Observe: Pluto's image changes to a photo of an American bald eagle,
     titled _I am smiling_

## Code Inspection ##

  See my //TODO comments: **View > Tool Windows > TODO**

## Source Code ##

  Available from GitHub:

  https://github.com/hurdleg/PlanetsRetrofit.git

## Reference ##

  Based on Retrofit in _Connecting Android Apps to RESTful Web Services_ with David Gassner
