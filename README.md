# Getting Started

Download repo (you're going to have to run both apps at the same time)

install java jdk, I'm using 20, so imagine you'll probably want to have that

Then from project directory run:

./mvnw install
./mvnw spring-boot:run

### in the interest of time:
* Didn't do as much error handling as I would in production and you should never catch a broad exceptions,</br>
but there's so many exceptions to catch, so I kept it simple and am assuming an ideal system
  </br>
  </br>
* Would have created an error response handler, so that you could just throw errors and it will send the responseEntity </br>
with that error message
  * assuming more ideal scenario that for most failures I just don't return anything
</br>
    </br>
* added comments in cases where I did something that could have been done better if not for the interest of time

### Also
* there's a bunch of xmls in here that are the full dunkin xml file just in various lengths, I also noticed there were
  </br>
  zip code issues, so I replaced all the bad zip codes with a usuable one
  * I use the baby one, because it's the quickest.
    * the full one I think takes like 45 minutes, just the nature of a rate limit  