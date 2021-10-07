# idea-getin-leasing

Crawling/scraping of all auctions of Idea Getin Leasing exceeds maximum lifetime of AWS Lambda, so there is need to split downloading data for at least 4 tasks with diffferent "startPage" inputParameter values. Proposed values:

https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-2?price=;50000

https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-1?price=50000%3B75000

https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-1?price=75000%3B110000

https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-1?price=110000%3B
