import requests
from bs4 import BeautifulSoup
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
def get_urls(n):
    '''
    获取要爬取的url
    n:页数
    '''
    urls = []
    for i in range(1,n+1):
        url = "https://travel.qunar.com/p-cs300021-nanchang-meishi?page=%s" % i
        urls.append(url)
    return urls

def get_information(url):
    '''
    url:要解析的网址
    '''
    res = requests.get(url)
    soup = BeautifulSoup(res.text,'lxml')
    lists = soup.find("ul",class_="list_item clrfix").find_all("li")

    data = []
    for i in lists:
        dic = {}
        dic['lat'] = i['data-lat']
        dic['lng'] = i['data-lng']
        dic['餐厅名称'] = i.find('span',class_='cn_tit').text
        dic['星级'] = i.find('span',class_='cur_star')['style'].split(':')[1]
        data.append(dic)
    return data

def get_alldata(n):
    '''
    采集n页的所有数据
    n:页数
    '''
    alldata = []
    for u in get_urls(n):
        '''extend是将数组元素一个一个加到另一个数组，append是将整个数组拼接到另一个数组'''
        alldata.extend(get_information(u))
    return pd.DataFrame(alldata)

# df = get_alldata(10)
# df.to_csv("E:\\南昌餐厅.csv",index=False,encoding='utf-8-sig')

df2 = pd.read_csv("E:\\南昌餐厅.csv")
'''
loc:筛选数据，','前面是行，':'表示所有行

'''
df2.loc[:,'lat'] = df2['lat'].astype('float')
df2.loc[:,'lng'] = df2['lng'].astype('float')
df2.loc[:,'星级'] = df2['星级'].str.replace('%','').astype('float')

df2 = df2.loc[df2['星级']!=0].reset_index(drop=True)
df2['星级'].hist(bins=20)     #pandas绘图：直方图

plt.show()

#df2.to_csv("E:\\南昌餐厅星级数据.csv",index=False,encoding='utf-8-sig')

