class A(object):
    x:int = 2

    def __init__(self:A, x:int):
        self.x = x
        print("El constructor de A funciona")

    def sum(self:A, x:int)->int:
        return self.x + x

class B(A):
    y:int = 2

    def __init__(self:B, y:int):
        self.y = y
        print("El constructor de B funciona")

    def sum(self:B, y:int)->int:
        return self.y * y
a:A = None
b:B = None
b = B(10)
a = A(10)
print(a.sum(2))
print(b.sum(2))