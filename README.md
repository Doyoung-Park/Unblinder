# Unblinder
Delivery Service App for Blind People


## 1. Backgorund & Objective
'Unblinder'<br/>
동사) 시각장애인과 비시각장애인의 경계를 없앤다.

![order30](https://user-images.githubusercontent.com/94168462/173596294-8c66af9b-51b1-4c56-8ea4-dc9628696e1d.png)
As marked red on the image, it takes almost 30minutes to order a delivery food from a normal delivery app for blinders
![guiNo](https://user-images.githubusercontent.com/94168462/173597115-f4eb6e18-0d74-4e38-ac1b-bf76758cd799.png)
Additionally, menu and store information on GUI does not help blinders.

To figure it out, we needed an app which runs with ARS UI so that it can info blinders store and menu information through a vocal way.
#### *Our ARS UI Structure*
![ars](https://user-images.githubusercontent.com/94168462/173598473-a837a631-c7be-40f9-a5fa-2d15aa10b5b3.png)

## 2. The way to gather store and menu information
Web Crawling -> But where?
We used the 'Naver Map' which allows crawling and has plenty of information about stores and menus.

But the problem was, there was no gathred information available on any page.
To figure it out, we hunted pages that contains the information we need. In result, we used multiple pages to gather the information we want. Just like the image below.
![](https://user-images.githubusercontent.com/94168462/173600819-0818fee0-2710-4cb1-bb37-a69c05c74bcc.png)

## 3.Key Functions
![](https://user-images.githubusercontent.com/94168462/173601949-3c1330f6-482a-4abf-a266-5bd361ddd5f0.png)

## 4. How it looks like
### Starting Page
![](https://user-images.githubusercontent.com/94168462/173602526-ecdcc29e-6ab3-4b9c-8d34-63f4a0b1a590.png)
### How to use page
![](https://user-images.githubusercontent.com/94168462/173602697-aff3fafb-085b-43e6-938e-483d3fb3be74.png)
### Page that comes out when a user says number one on the initial screen
At this page, a user can say about what kind of food he/she wants.<br/>
![](https://user-images.githubusercontent.com/94168462/173603174-24fd0116-28f8-4731-86e9-36665599bda5.png)
### Page that comes oyt after a user said '치킨'
Based on GPS, it searches '치킨' stores near the user on the web.<br/>
![](https://user-images.githubusercontent.com/94168462/173603553-44f6bb29-5b0c-4d02-9431-e883796af89d.png)
### Page that comes oyt after a user selected #1 store
User can get informed menus of the restaurant and can call to the restaurant.<br/>
![](https://user-images.githubusercontent.com/94168462/173604152-f5ec077c-28f3-4c0b-a42b-f81012e230b8.png)

## 5. Future Works
![](https://user-images.githubusercontent.com/94168462/173604795-3a8ef890-72f6-4462-b424-70ae892c7b70.png)

## 6.Team member info
|Student ID|Name|Role|
|------|---|------|
|201835416|김동민|TTS, STT, Trailer Video|
|201835435|김주형|Web Crawling(Restaurant), Presentation|
|201835453|박도영|User Interface, PPT Slides|
|201835459|박준영|Web Crawling(Menus), Video Editing|
