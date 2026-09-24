from pathlib import Path

source = Path('/home/ubuntu/anton-service-pos/client/src/pages/Home.tsx').read_text()
terms = ['subtotal', 'discount', 'tax', 'taxAmount', 'jasaTotal', 'spareTotal', 'hppTotal']
for term in terms:
    print(f'--- {term} ---')
    start = 0
    count = 0
    while count < 8:
        index = source.lower().find(term.lower(), start)
        if index < 0:
            break
        left = max(0, index - 360)
        right = min(len(source), index + 520)
        print(source[left:right].replace('\n', ' ') + '\n')
        start = index + len(term)
        count += 1
