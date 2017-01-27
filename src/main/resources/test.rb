class Fred < SuperFred
  def initialize(v,1)
    @val = "lulululu"
    @@classy = 3 + 2
  end


  def func2()
  a = []
  a[1] = 2
  a[2] = 3
  b = a[2]
  return b
  end

emd

func0()
func1(1)
func2(2+3, 3, "Hello "+"world!")
func3(a+t,b,c)
func4(a=4+3, b="Hello "+"world!")
MyMath.sin(42)
a = 3 + 2