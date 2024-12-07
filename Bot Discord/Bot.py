#Improved bot
#By: Robbie and DoctorSneus

import discord
import youtube_dl
import asyncio
import os
import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
from dotenv import load_dotenv
from discord.ext import commands
from youtubesearchpython.__future__ import VideosSearch


#Dictionary where the key is the id to every guild where the bot is currently playing
#Every key is gonna be associated with a list of songs which are on the queue
queues = {}

class Song:
    def __init__(self, guild_id: int , text: discord.TextChannel, voice: discord.VoiceChannel , idvideo: str, video_title: str, embed: dict):
        self.guild_id = guild_id
        self.text = text
        self.idvideo = idvideo
        self.video_title = video_title
        self.voice = voice
        self.send_message = True
        self.embed = embed
        
    async def play(self):

        #Function to play after the song is done
        def afterFunc(error = None):
            if(error):
                print(error)
            else:
                #Check if the queue exists
                if self.guild_id in queues.keys():
                    #Checks if the queue is not empty
                    if queues[self.guild_id]:
                        asyncio.run_coroutine_threadsafe(queues[self.guild_id].pop(0).play(),client.loop)
                    else:
                        #If empty we delete it from the dictionary and disconnect
                        del queues[self.guild_id]
                        asyncio.run_coroutine_threadsafe(self.voice.disconnect(),client.loop)
                else:
                    asyncio.run_coroutine_threadsafe(self.voice.disconnect(),client.loop)

        self.voice.play(discord.PCMVolumeTransformer(discord.FFmpegPCMAudio(self.idvideo)),after = afterFunc)
        if self.send_message:
            await self.text.send(embed=self.get_embed())  
    
    def set_send_message(self, send_message: bool = True ):
        self.send_message = send_message

    def get_videoTitle(self):
        return self.video_title
    
    def get_embed(self):
        return discord.Embed.from_dict(self.embed)

    def __str__(self):
        return ('Now playing: {}'.format(self.video_title))


guilds = [715759799668834324, 658165266206818315]

client = discord.Bot(debug_guilds=guilds)
ytdl_format_options_video = {
    'format': 'bestaudio/best',
    'outtmpl': '%(id)s.%(ext)s',
    'restrictfilenames': True,
    'nocheckcertificate': True,
    'ignoreerrors': False,
    'quiet': True,
    'no_warnings': True,
    'nooverwrites': True,
    'cachedir' : False,
    'noplaylist': True,
    'source_address' : '0.0.0.0',
}



ytdl = youtube_dl.YoutubeDL(ytdl_format_options_video)

async def search_song(title):
    result = await VideosSearch(query = title,limit = 1).next()
    return result['result']

# Checks before running commands
async def author_is_connected(ctx):
    print(ctx)
    if ctx.author.voice and ctx.author.voice.channel:
        return True
    else:
        await ctx.response.send_message("You gotta be in a voice channel pal")
        return False

async def bot_is_connected(ctx):
    if ctx.guild.voice_client:
        return True
    else:
        await ctx.response.send_message("I'm not connected pal")
        return False

async def connect_bot(ctx):
    #Checks if the bot is connected to a voicechannel in the server
    if ctx.guild.voice_client == None:
        await ctx.author.voice.channel.connect()
    return True
    
async def connected_same_channel(ctx):
    #Checks if the author and the bot are in the same channel
    if ctx.guild.voice_client.channel != ctx.author.voice.channel:
        await ctx.response.send_message("You gotta be in the same voice channel pal")
        return False
    else:
        return True

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))
     

async def genericPlay(ctx, url):
            
    #Stream music
    info_dict_complete = await client.loop.run_in_executor(
            None, lambda: ytdl.extract_info(url, download=False)
        )
    response = False

    if 'entries' in info_dict_complete:
        entries_num = len(info_dict_complete['entries'])
        response = True
        m = await ctx.interaction.original_message()
        await m.edit(content = "Adding \"{}\" ({} songs found)".format(info_dict_complete['title'],entries_num))
    else:
        info_dict_complete['entries'] = [info_dict_complete]
        entries_num = 1

        
    for i in range(entries_num):
        info_dict = info_dict_complete['entries'][i]
        video_title = info_dict.get('title', None)
        idvideo = info_dict.get('url',None)

        #Check if the url was valid
        if not idvideo:
            m = await ctx.interaction.original_message()
            await m.edit(content = 'Couldn\'t find that song bucko')
            return

        #Create song object
        voice = ctx.guild.voice_client
        embed_dict = {'title': 'Now Playing', 'description': video_title, 'url' : info_dict['webpage_url'], 'color': 5763719, 'author':{'name':ctx.author.display_name,'icon_url':ctx.author.display_avatar.url }, 'thumbnail': {'url': info_dict['thumbnail']}}
        s = Song(ctx.guild.id, ctx.channel, voice, idvideo, video_title, embed_dict)

        #Check if the bot has a queue in the server
        if not(ctx.guild.id in queues.keys()):
            queues[ctx.guild.id] = []
            if not response:
                response = True
                s.set_send_message(send_message = False)
                m = await ctx.interaction.original_message()
                await m.edit(content = None, embed = discord.Embed.from_dict(embed_dict))
            asyncio.run_coroutine_threadsafe(s.play(),client.loop)
            
        else:
            queues[ctx.guild.id].append(s)
            if not response:
                response = True
                m = await ctx.interaction.original_message()
                await m.edit(content = 'Added: {}'.format(video_title))


@client.slash_command(description = "Play a song from a youtube search pal")
@commands.guild_only()
@commands.check(connected_same_channel)
@commands.check(connect_bot)
@commands.check(author_is_connected)
async def play( ctx, 
                query: discord.Option(str, "What do you want to search for, pal?")):
    await ctx.interaction.response.defer()

    # Finding the URL
    result = await search_song(query)
    if result:
        await genericPlay(ctx, result[0]["link"])
    else:
        m = await ctx.interaction.original_message()
        await m.edit(content = 'Couldn\'t find that song bucko')

@client.slash_command(description = "Play a song from a URL pal")
@commands.guild_only()
@commands.check(connected_same_channel)
@commands.check(connect_bot)
@commands.check(author_is_connected)
async def play_url( ctx, 
                url: discord.Option(str, "From what link do you want me to play, pal?")):
    await ctx.interaction.response.defer()
    await genericPlay(ctx, url)


@client.slash_command(description = "Kill the bot pal")
async def bye(ctx):
    await ctx.response.send_message("Good night")
    for server in client.guilds:
        if server.voice_client:
            await server.voice_client.disconnect()
    await client.close()   

# Pause command
@client.slash_command(description = "Pause the current song pal")
@commands.check(connected_same_channel)
@commands.check(bot_is_connected)
@commands.check(author_is_connected)
async def pause(ctx):
    # Song is playing
    if not ctx.guild.voice_client.is_paused():
        ctx.guild.voice_client.pause()
        await ctx.response.send_message("Pausing song pal")
    
    # Song is paused
    else:
        await ctx.response.send_message("Can't pause pal")

# Unpause command
@client.slash_command(description = "Unpause the current song pal")
@commands.check(connected_same_channel)
@commands.check(bot_is_connected)
@commands.check(author_is_connected)
async def unpause(ctx):
    # Song is paused
    if ctx.guild.voice_client.is_paused():
        ctx.guild.voice_client.resume()
        await ctx.response.send_message("Unpausing song pal")

    # No songs are paused
    else:
        await ctx.response.send_message("Can't unpause pal")

# Skip command
@client.slash_command(description = "Skip the current song pal")
@commands.check(connected_same_channel)
@commands.check(bot_is_connected)
@commands.check(author_is_connected)
async def skip(ctx):
    if ctx.guild.voice_client.is_playing() or ctx.guild.voice_client.is_paused():
        ctx.guild.voice_client.stop()
        await ctx.response.send_message("Skipping song pal")
    else:
        await ctx.response.send_message("Nothing is playing pal")

# Stop command
@client.slash_command(description = "No more music pal")
@commands.check(connected_same_channel)
@commands.check(bot_is_connected)
@commands.check(author_is_connected)
async def stop(ctx):
    if ctx.guild.voice_client.is_playing() or ctx.guild.voice_client.is_paused():
        del queues[ctx.guild.id]
        ctx.guild.voice_client.stop()
        await ctx.response.send_message("Stopped queue pal")
    else:
        await ctx.response.send_message("Nothing is playing pal")

@client.slash_command(description = "Show the songs on queue pal")
@commands.guild_only()
@commands.check(bot_is_connected)
async def show_queue(ctx):
    #Shows the queue of songs
    if ctx.guild.id in queues.keys() and queues[ctx.guild.id]: 
        await ctx.defer()
        string = ""
        # For each song in the queue, it appends the enumerated title
        for i, song in enumerate(queues[ctx.guild.id]):
            string += f"{i+1}. {song.get_videoTitle()}\n"
            
        m = await ctx.interaction.original_message()
        await m.edit(content = string)

    else:
        await ctx.response.send_message("Queue is empty pal")
    
@client.slash_command(description = "Delete the songs in queue pal")
@commands.check(connected_same_channel)
@commands.check(bot_is_connected)
@commands.check(author_is_connected)
async def delete_queue(ctx):
    if ctx.guild.id in queues.keys() and queues[ctx.guild.id]: 
        del queues[ctx.guild.id]
        await ctx.response.send_message("Deleted queue pal")
    else:
        await ctx.response.send_message("There's no queue pal")

@client.slash_command(description = "Play a song or a playlist from spotify pal")
@commands.guild_only()
@commands.check(connected_same_channel)
@commands.check(connect_bot)
@commands.check(author_is_connected)
async def play_spotify( ctx, 
                url: discord.Option(str, "From what link do you want me to play, pal?"),
                index: discord.Option(int, "What song do you want to start at, pal?", required=False, default = 1),
                limit: discord.Option(int, "How many songs do you want to play, pal?", required=False, default = 50),
                randomize: discord.Option(bool, "Do you want me to play the songs in random order, pal?", required=False, default = False)):
    await ctx.interaction.response.defer()
    playlist = sp.playlist_items(url, offset = index-1, limit = limit ,additional_types=["track"])
    if playlist:
        for song in playlist["items"]:
            result = await search_song(song["track"]["name"] + ' ' + song["track"]["artists"][0]["name"])
            await genericPlay(ctx, result[0]["link"])
    else:
        m = await ctx.interaction.original_message()
        await m.edit(content = 'Couldn\'t find that playlist bucko')




    
load_dotenv('.env')
auth_manager = SpotifyClientCredentials()
sp = spotipy.Spotify(auth_manager=auth_manager)
client.run(os.environ['DISCORD_TOKEN'])