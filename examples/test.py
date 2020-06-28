def a()->str:
    for y in [1,2,3,4]:
        print(y)
        if(y > 2):
            return "funciona"
    return "no funciona"

print(a())