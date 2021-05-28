import pandas as pd 
import shutil

data = pd.read_csv("metadata.csv") 
# Preview the first 5 lines of the loaded data 
data.head()

data = data[data['title'].str.contains("coronavirus", na=False)]
data = data[data['pdf_json_files'].notna()]
data = data[data['publish_time'].str.contains("2021", na=False)]
data = data[data['pdf_json_files'].notna()]
f = open("date.txt", "a")

for key, value in data.iteritems():
    if key == "pdf_json_files":
        json = value.str.split('/').to_list()
    if key == "publish_time":
        dates = value.to_list()
for i in range(len(dates)):
    f.write(str(dates[i])+'\n')
f.close()


json_files = []
for i in range(len(json)):
    array = json[i][2].split(';')
    json_files.append("C:\\Users\\Panagiotis\\Desktop\\anak\\pdf_json\\" + array[0])


for f in json_files:
    shutil.move(f, 'corpus')