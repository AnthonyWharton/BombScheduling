import praw
# import json

reddit = praw.Reddit(client_id='QNODIY8pI3SAHw',
                     client_secret='KgKRGwhIN1HUEpdRFSLtSPSIdu8',
                     password='cykablyat3',
                     username='BombScheduling',
                     user_agent='Sending messages (to people that want them) by /u/BombScheduling, StudentHackV project')


# user = praw.models.Redditor(reddit, user="evilpenguinsinspace")

# reddit.redditor("evilpenguinsinspace").message("Get Rekt on the regular", "Oh wait you already do.")


# class Object:


# class MessageReddit(object):
#     def fromJSON(self):

# def toJSON(obj):
#     return json.dumps(obj, default=lambda o: o.__dict__,
#         sort_keys=True, indent=4)
#
# def fromJSON(obj):
#     if '__type__' in obj and obj['__type__'] == 'User':
#         return User(obj['name'], obj['username'])
#     return obj

# class

class RedditMessage():
    def __init__(self, reddit_username, reddit_message_title, reddit_message_body):
        self.reddit_username      = reddit_username
        self.reddit_message_title = reddit_message_title
        self.reddit_message_body  = reddit_message_body

def send(msg):
    reddit.redditor(msg.reddit_username).message(msg.reddit_message_title, msg.reddit_message_body)

def test():
    msg = RedditMessage("evilpenguinsinspace", "Howdy", "Hello, World!")
    send(msg)

test()


# class Dog():
#     def __init__(self, value, values):
#         self.value = value
#         self.values = values

# dog = Dog(12, ["Apple", 7])
#
# dog1 = toJSON(dog)

# dogthing = {
#     "value": 12,
#     "values": [
#         "Apple",
#         7
#     ]
# }


# print(dog1)
#
# dog2 =  fromJSON(dog1)
# print(dog2)
