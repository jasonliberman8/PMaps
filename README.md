# PMaps
A Principled, Powerful Collection of Key-Value Pairs


PMaps are a simple collection of key-values pairs. They are an attempt to remedy some potentially 
undesirable aspects of native immutable Scala Maps, in addition to offering some powerful syntax 
for robust and expressive code authoring.

#### In an attempt to be 'P'rincipled, PMaps offer:

1) No unsafe operations that will drop keys silently (key addition, flatMap) 
2) Transparent Defaults: Differences in the types of PMaps that are partial functions vs. actual functions

#### In an attempt to be 'P'owerful, PMaps offer:

1) Algebraic operations at the collection level
2) 'Join' functionality that enables some safe, but powerful SQL-like joins on the keys of PMaps



