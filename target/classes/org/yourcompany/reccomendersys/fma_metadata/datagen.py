import csv
import random
from datetime import datetime, timedelta
import numpy as np

# Set random seed for reproducibility
random.seed(42)
np.random.seed(42)

# Generate Tracks Data
genres = ["Pop", "Rock", "Hip Hop", "R&B", "Country", "Electronic", "Jazz", "Classical", "Indie", "Metal", "Folk", "Blues", "Reggae", "Latin", "K-Pop"]
years = list(range(1960, 2025))

artists = [
    "Taylor Swift", "Drake", "The Weeknd", "Beyoncé", "Bad Bunny", "Ariana Grande", "BTS", "Ed Sheeran", 
    "Billie Eilish", "Post Malone", "Dua Lipa", "Justin Bieber", "Kendrick Lamar", "Harry Styles", "Rihanna",
    "Adele", "Bruno Mars", "J Balvin", "Travis Scott", "Coldplay", "Lady Gaga", "The Beatles", "Queen",
    "Michael Jackson", "Elton John", "David Bowie", "Prince", "Madonna", "Fleetwood Mac", "Led Zeppelin",
    "Pink Floyd", "Frank Sinatra", "Miles Davis", "Bob Dylan", "Johnny Cash", "Aretha Franklin", "Ray Charles",
    "Nina Simone", "John Coltrane", "Stevie Wonder", "Marvin Gaye", "Bob Marley", "Tupac Shakur", "Notorious B.I.G.",
    "Jay-Z", "Eminem", "Kanye West", "Radiohead", "Nirvana", "Pearl Jam", "Metallica", "AC/DC", "Black Sabbath",
    "Foo Fighters", "Red Hot Chili Peppers", "Green Day", "Tame Impala", "Arctic Monkeys", "The Strokes"
]

album_templates = [
    "{} (Deluxe Edition)", "The Best of {}", "{} World Tour", "Greatest Hits", "{} Unplugged",
    "{} (Remastered)", "Live at {}", "{} Sessions", "The {} Experience", "{} Vol. 1",
    "Return of {}", "{} 2.0", "Welcome to {}", "Midnight {}", "Summer of {}", 
    "Lost in {}", "{} Dreams", "Forever {}", "The {} Diaries", "{} Days",
    "{} Nights", "Love & {}", "{} Theory", "Evolution of {}", "{} Revolution"
]

track_title_templates = [
    "The {} Song", "{} Dance", "Lost in {}", "{} Dreams", "Forever {}",
    "Beautiful {}", "{} Nights", "Love {}", "Broken {}", "{} Heart",
    "Sweet {}", "Crying {}", "{} Blues", "{} Party", "Midnight {}",
    "Summer {}", "Winter {}", "{} Rain", "{} Sunshine", "Morning {}",
    "{} World", "{} Paradise", "{} Memories", "{} Tears", "{} Smile",
    "The Last {}", "First {}", "{} Story", "{} Adventure", "Magic {}",
    "{} Fantasy", "{} Reality", "Secret {}", "{} Mystery", "{} Wonder",
    "Endless {}", "{} Journey", "{} Road", "{} Streets", "{} Highway",
    "{} River", "{} Ocean", "{} Mountain", "{} Sky", "{} Stars",
    "{} Moon", "{} Sun", "{} Universe", "{} Infinity", "{} Eternity",
    "{} Yesterday", "{} Today", "{} Tomorrow", "{} Forever", "{} Always"
]

nouns = [
    "Love", "Heart", "Dream", "Night", "Day", "Sky", "Life", "Time", "Soul", "Mind",
    "Fire", "Water", "Earth", "Wind", "Light", "Shadow", "Star", "Moon", "Sun", "Angel",
    "Devil", "Heaven", "Hell", "Kiss", "Touch", "Dance", "Song", "Sound", "Silence", "Word",
    "Memory", "Hope", "Faith", "Trust", "Friend", "Home", "Road", "Journey", "Adventure", "Story",
    "Pain", "Joy", "Smile", "Tear", "Eye", "Hand", "Face", "Voice", "Whisper", "Scream",
    "Ghost", "Spirit", "Magic", "Wonder", "Rainbow", "Storm", "Ocean", "River", "Mountain", "Forest"
]

# Feature artists pool - subset of main artists plus additional names
feature_artists = artists[:30] + [
    "Cardi B", "Megan Thee Stallion", "Lil Nas X", "Doja Cat", "SZA", "21 Savage", "Malone",
    "Halsey", "Chance the Rapper", "Lil Baby", "DaBaby", "Roddy Ricch", "Future", "Young Thug",
    "Nicki Minaj", "Pharrell Williams", "Snoop Dogg", "Ice Cube", "Dr. Dre", "Anderson .Paak"
]

# Generate 200+ tracks
tracks = []
track_ids = []
for i in range(1, 250):
    track_id = f"T{i:04d}"
    track_ids.append(track_id)
    
    artist = random.choice(artists)
    genre = random.choice(genres)
    year = random.choice(years)
    
    # Generate album name
    album_template = random.choice(album_templates)
    album_noun = random.choice(nouns)
    album = album_template.format(album_noun)
    
    # Generate track title
    title_template = random.choice(track_title_templates)
    title_noun = random.choice(nouns)
    title = title_template.format(title_noun)
    
    # Decide if track has features (20% chance)
    features = ""
    if random.random() < 0.2:
        num_features = random.randint(1, 3)  # 1 to 3 features
        feature_list = random.sample(feature_artists, num_features)
        # Make sure the feature isn't the same as the main artist
        feature_list = [f for f in feature_list if f != artist]
        if feature_list:
            features = ", ".join(feature_list)
    
    tracks.append([track_id, title, artist, album, year, genre, features])

# Generate Users Data
usernames = [
    "musiclover", "soundwave", "beatmaster", "melodymaker", "rhythmking", "songbird", "tunesmith", 
    "audiophile", "basshead", "vocalista", "djmixer", "groovemaster", "harmonyhunter", "lyricist", 
    "rockstar", "popfan", "jazzcat", "classicalbuff", "hiphophead", "metalmaniac", "folkie", 
    "bluesbrother", "reggaeroots", "latinlover", "kpopstan", "playlist_pro", "albumartist", 
    "concertgoer", "vinylcollector", "streamingstar", "charttopper", "indiefan", "headbanger", 
    "soulseeker", "funkyfresh", "dancepartner", "electronica", "rapfan", "countrygirl", 
    "poproyalty", "rockon", "jazzmaster", "classicalking", "hiphopqueen", "metalhead", 
    "folkfan", "bluesqueen", "reggaeking", "latinrhythm", "kpopfanatic"
]

locations = [
    "New York, USA", "Los Angeles, USA", "London, UK", "Tokyo, Japan", "Paris, France", 
    "Berlin, Germany", "Sydney, Australia", "Toronto, Canada", "Seoul, South Korea", 
    "Mexico City, Mexico", "Rio de Janeiro, Brazil", "Mumbai, India", "Shanghai, China", 
    "Cape Town, South Africa", "Stockholm, Sweden", "Amsterdam, Netherlands", "Dubai, UAE", 
    "Singapore", "Moscow, Russia", "Barcelona, Spain", "Rome, Italy", "Athens, Greece", 
    "Cairo, Egypt", "Bangkok, Thailand", "Dublin, Ireland", "Oslo, Norway", "Vienna, Austria", 
    "Helsinki, Finland", "Lisbon, Portugal", "Brussels, Belgium", "Copenhagen, Denmark",
    "Chicago, USA", "Miami, USA", "Austin, USA", "Nashville, USA", "Seattle, USA",
    "Vancouver, Canada", "Manchester, UK", "Glasgow, UK", "Edinburgh, UK", "Munich, Germany",
    "Milan, Italy", "Hong Kong", "Kyoto, Japan", "Melbourne, Australia", "Auckland, New Zealand",
    "Johannesburg, South Africa", "Buenos Aires, Argentina", "Santiago, Chile", "Sao Paulo, Brazil"
]

demographics = [
    "student", "professional", "musician", "artist", "teacher", "healthcare worker", 
    "tech industry", "hospitality", "retail", "finance", "engineering", "marketing", 
    "education", "entrepreneur", "retired", "creative industry", "service industry",
    "freelancer", "government employee", "non-profit worker"
]

genders = ["Male", "Female", "Non-binary", "Prefer not to say"]

# Generate 100+ users
users = []
user_ids = []

for i in range(1, 125):
    user_id = f"U{i:04d}"
    user_ids.append(user_id)
    
    username = random.choice(usernames) + str(random.randint(1, 999))
    age = random.randint(18, 70)
    gender = random.choice(genders)
    location = random.choice(locations)
    demographic = random.choice(demographics)
    
    users.append([user_id, username, age, gender, location, demographic])

# Generate Interactions Data
interaction_types = ["stream", "like", "add_to_playlist", "download", "share", "skip", "complete_listen", "review"]

# Create timestamps spanning the last 6 months
end_date = datetime.now()
start_date = end_date - timedelta(days=180)
date_range = (end_date - start_date).days

interactions = []
for user_id in user_ids:
    # Each user has between 10 and 20 interactions
    num_interactions = random.randint(10, 20)
    for _ in range(num_interactions):
        track_id = random.choice(track_ids)
        # Rating is 1-5 with bias toward higher ratings
        rating_weights = [0.05, 0.1, 0.2, 0.3, 0.35]  # Probability weights for ratings 1-5
        rating = np.random.choice(range(1, 6), p=rating_weights)
        
        # Generate random timestamp within the last 6 months
        random_days = random.randint(0, date_range)
        random_date = start_date + timedelta(days=random_days)
        timestamp = random_date.strftime('%Y-%m-%d %H:%M:%S')
        
        interaction_type = random.choice(interaction_types)
        
        interactions.append([user_id, track_id, rating, timestamp, interaction_type])

# Write to CSV files
with open('tracks.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['track_id', 'title', 'artist', 'album', 'year', 'genre', 'features'])
    writer.writerows(tracks)

with open('users.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['user_id', 'username', 'age', 'gender', 'location', 'demographics'])
    writer.writerows(users)

with open('interactions.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['user_id', 'track_id', 'rating', 'timestamp', 'interaction_type'])
    writer.writerows(interactions)

print(f"Generated {len(tracks)} tracks")
print(f"Generated {len(users)} users")
print(f"Generated {len(interactions)} interactions")

# Preview of data
print("\nTracks preview:")
for i in range(min(5, len(tracks))):
    print(tracks[i])

print("\nUsers preview:")
for i in range(min(5, len(users))):
    print(users[i])

print("\nInteractions preview:")
for i in range(min(5, len(interactions))):
    print(interactions[i])