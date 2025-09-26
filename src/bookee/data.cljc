(ns bookee.data)

(def services
  [{:id           1
    :service-name "Men's Regular Cut"
    :duration     30
    :price        40
    :currency     :usd
    :details      "This is a very good service"}
   {:id           2
    :service-name "Buzz Cut"
    :duration     30
    :price        35
    :currency     :usd
    :details      "This is a very good service as well, trust me"}
   {:id           3
    :service-name "Layered Cut"
    :duration     30
    :price        45
    :currency     :usd
    :details      "The best cut ever"}
   {:id           4
    :service-name "Wash, Cut and Style"
    :duration     30
    :price        55
    :currency     :usd
    :details      "You get what you pay for"}
   {:id           5
    :service-name "Shave"
    :duration     30
    :price        40
    :currency     :usd
    :details      "Hey you need a shave?"}
   {:id           6
    :service-name "Haircut+Shave"
    :duration     60
    :price        70
    :currency     :usd
    :details      "You wish to get a haircut and a shave?"}])

(def currencies
  {:usd  "$"
   :euro "€"})

(def users
  [{:id               10
    :name             "Jeff"
    :surname          "Jefferson"
    :details          "Jeff is solid!"
    :img              "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=4&w=256&h=256&q=80"
    :services-offered [1 2 3 4 5 6]}
   {:id               11
    :name             "Jenny"
    :surname          "Jonathan"
    :details          "Da best!"
    :img              "https://tailwindcss.com/_next/static/media/erin-lindford.90b9d461.jpg"
    :services-offered [1 2 3 4]}
   {:id               9
    :name             "Jackie"
    :surname          "Ripper"
    :img              "https://images.unsplash.com/photo-1605405748313-a416a1b84491?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=facearea&facepad=4&w=256&h=256&q=80"
    :details          "Best of the bestest!"
    :services-offered [1 2 3]}])

(def team-ids [9 10 11])

(def reviews
  [{:id     1
    :author "Gordon Ramsay"
    :rating 5
    :review "Finally, a cut as sharp as my knives! This haircut is bloody brilliant!"
    :date   "seconds ago"}
   {:id     2
    :author "Severus Snape"
    :rating 5
    :review "Obviously... the most precise trim I've encountered. Ten points to this barbershop."
    :date   "5 hours ago"}
   {:id     3
    :author "Johnny Bravo"
    :rating 5
    :review "Do the monkey with me! My hair looks even MORE magnificent now. Mama would be proud!"
    :date   "Yesterday"}
   {:id     4
    :author "Gandalf the Grey"
    :rating 5
    :review "You shall not pass... without getting your beard trimmed here! Simply magical."
    :date   "2 days ago"}
   {:id     5
    :author "Walter White"
    :rating 5
    :review "The chemistry is perfect. 99.1% pure satisfaction. You're goddamn right this is the best cut."
    :date   "3 days ago"}
   {:id     6
    :author "Dwight Schrute"
    :rating 5
    :review "Fact: This is the superior barbershop. Bears. Beets. Best haircuts."
    :date   "4 days ago"}
   {:id     7
    :author "Ron Swanson"
    :rating 4
    :review "I normally cut my own hair, but this was acceptable. They didn't talk too much."
    :date   "5 days ago"}
   {:id     8
    :author "Goku"
    :rating 5
    :review "My hair has reached a power level over 9000! Even better than Super Saiyan style!"
    :date   "1 week ago"}
   {:id     9
    :author "Jack Sparrow"
    :rating 5
    :review "This is the day you will always remember as the day you got the best haircut from... wait, where's the rum?"
    :date   "1 week ago"}
   {:id     10
    :author "Sherlock Holmes"
    :rating 5
    :review "Elementary! The precision, the attention to detail, the perfect angle of each cut. Fascinating."
    :date   "2 weeks ago"}
   {:id     11
    :author "Spaliaras"
    :rating 4
    :review "ΕΜΠ1.... γία κούρεμα φυσικά!"
    :date   "3 weeks ago"}])